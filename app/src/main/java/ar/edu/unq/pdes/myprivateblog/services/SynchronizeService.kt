package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import com.google.android.gms.common.util.Base64Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import io.reactivex.Flowable
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import java.io.*
import java.util.*
import javax.crypto.SecretKey
import javax.inject.Inject

interface SyncService {
    fun updateBlogsFireBase(currentUser: FirebaseUser,lifecycleOwner: LifecycleOwner)
    fun updateBlogsLocalBase(currentUser: FirebaseUser, spinner: ProgressBar)
    fun deleteBlogsLocalBase(currentUser: FirebaseUser,lifecycleOwner: LifecycleOwner)
    fun syncWithFireBase(spinner: ProgressBar,lifecycleOwner: LifecycleOwner)
}

class SynchronizeService @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context,
    val encryptionService: EncryptionService,
    val postService: PostService) : SyncService{
    lateinit var getBlogsObserver : Observer<List<BlogEntry>>
    lateinit var deleteBlogsObserver : Observer<List<BlogEntry>>
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun updateBlogsFireBase(currentUser: FirebaseUser, lifecycleOwner: LifecycleOwner) {
        val blogEntries = blogEntriesRepository.getBlogEntriesWith(false, false)
        getBlogsObserver = Observer<List<BlogEntry>> { unsyncBlogs ->
            db.runBatch { batch ->
                unsyncBlogs.forEach {
                    val path = it.bodyPath
                    var body : String = ""
                    if(path != null) {
                        body = File(context.filesDir, path).readText()
                    }
                    val dbReference = db.collection(currentUser.uid).document(it.uid.toString())
                    val inputStreamBody = ByteArrayInputStream(
                        body.toByteArray(Charsets.UTF_8)
                    )
                    val outputStreamBody = ByteArrayOutputStream()
                    encryptionService.encrypt(inputStreamBody, outputStreamBody)
                    val encryptedBodyString = Base64Utils.encode(outputStreamBody.toByteArray())
                    val firebasePost = object {
                        var uid = it.uid
                        var title = it.title
                        var body = encryptedBodyString
                        var cardColor = it.cardColor
                        var deleted = it.deleted
                        var synced = it.synced
                    }
                    batch.set(dbReference, firebasePost, SetOptions.merge())
                }
            }.addOnSuccessListener {
                unsyncBlogs.forEach {
                    it.synced = true
                    blogEntriesRepository.updateBlogEntry(it)
                }
                blogEntries.removeObserver(this.getBlogsObserver)
            }.addOnFailureListener {
                Toast.makeText(context, "Falopaa", Toast.LENGTH_LONG).show()
                Timber.d(it)
            }
        }
        blogEntries.observe(lifecycleOwner,getBlogsObserver )
    }

    override fun updateBlogsLocalBase(currentUser: FirebaseUser, spinner: ProgressBar) {
        db.collection(currentUser.uid).whereEqualTo("deleted", false).get()
            .addOnSuccessListener { result ->
                val disp = Flowable.fromCallable {
                    val blogs = mutableListOf<BlogEntry>()
                    for(doc in result) {
                        val fileName = UUID.randomUUID().toString() + ".body"
                        val outputStreamWriter =
                            OutputStreamWriter(
                                context.openFileOutput(
                                    fileName,
                                    Context.MODE_PRIVATE
                                )
                            )
                        val encryptedBodyString = doc["body"] as String
                        val decryptInputStream = ByteArrayInputStream(
                            Base64Utils.decode(encryptedBodyString)
                        )
                        val decryptOutputStream = ByteArrayOutputStream()
                        encryptionService.decrypt(decryptInputStream, decryptOutputStream)
                        val body = decryptOutputStream.toByteArray().toString(Charsets.UTF_8)
                        outputStreamWriter.use { it.write(body) }
                        val possiblePost = doc.toObject(BlogEntry::class.java)
                        possiblePost.bodyPath = fileName
                        blogs.add(possiblePost)
                    }
                    blogs
                }.flatMapCompletable {
                    blogEntriesRepository.insertAll(it)
                }.compose(RxSchedulers.completableAsync()).subscribe({
                    spinner.visibility = View.GONE
                },{
                    if(it is IOException){
                        Toast.makeText(context, R.string.encryptor_error, Toast.LENGTH_LONG).show()
                    }
                    spinner.visibility = View.GONE
                    Timber.d(it)
                })
            }
    }

    override fun deleteBlogsLocalBase(currentUser: FirebaseUser,lifecycleOwner: LifecycleOwner) {
        val blogEntries = blogEntriesRepository.getBlogEntriesWith(true)
        deleteBlogsObserver = Observer{ deletedBlogs ->
            db.runBatch { batch ->
                deletedBlogs.forEach {
                    val dbReference = db.collection(currentUser.uid).document(it.uid.toString())
                    batch.delete(dbReference)
                }
            }.addOnCompleteListener {
                blogEntriesRepository.deleteAll(deletedBlogs)
                blogEntries.removeObserver(deleteBlogsObserver)
            }
        }
        blogEntries.observe(lifecycleOwner, deleteBlogsObserver)
    }

    override fun syncWithFireBase(spinner : ProgressBar, lifecycleOwner: LifecycleOwner){
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateBlogsFireBase(currentUser,lifecycleOwner)
            updateBlogsLocalBase(currentUser,spinner)
            deleteBlogsLocalBase(currentUser,lifecycleOwner)
        }
    }
}


class FakeSynchronizeService @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context,
    val encryptionService: EncryptionService,
    val postService: PostService) : SyncService{
    lateinit var getBlogsObserver : Observer<List<BlogEntry>>
    lateinit var deleteBlogsObserver : Observer<List<BlogEntry>>
    private val db: FirebaseFirestore by lazy {
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
// the host computer from an Android emulator.
        val settings : FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
        .setHost("10.0.2.2:8080")
        .setSslEnabled(false)
        .setPersistenceEnabled(false)
        .build()

        val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings
        firestore
    }
    val auth = FirebaseAuth.getInstance()
    override fun updateBlogsFireBase(currentUser: FirebaseUser, lifecycleOwner: LifecycleOwner) {
        val blogEntries = blogEntriesRepository.getBlogEntriesWith(false, false)
        getBlogsObserver = Observer<List<BlogEntry>> { unsyncBlogs ->
            db.runBatch { batch ->
                unsyncBlogs.forEach {
                    val path = it.bodyPath
                    var body : String = ""
                    if(path != null) {
                        body = File(context.filesDir, path).readText()
                    }
                    val dbReference = db.collection(currentUser.uid).document(it.uid.toString())
                    val inputStreamBody = ByteArrayInputStream(
                        body.toByteArray(Charsets.UTF_8)
                    )
                    val outputStreamBody = ByteArrayOutputStream()
                    encryptionService.encrypt(inputStreamBody, outputStreamBody)
                    val encryptedBodyString = Base64Utils.encode(outputStreamBody.toByteArray())
                    val firebasePost = object {
                        var uid = it.uid
                        var title = it.title
                        var body = encryptedBodyString
                        var cardColor = it.cardColor
                        var deleted = it.deleted
                        var synced = it.synced
                    }
                    batch.set(dbReference, firebasePost, SetOptions.merge())
                }
            }.addOnSuccessListener {
                unsyncBlogs.forEach {
                    it.synced = true
                    blogEntriesRepository.updateBlogEntry(it)
                }
                blogEntries.removeObserver(this.getBlogsObserver)
            }.addOnFailureListener {
                Toast.makeText(context, "Falopaa", Toast.LENGTH_LONG).show()
                Timber.d(it)
            }
        }
        blogEntries.observe(lifecycleOwner,getBlogsObserver )
    }

    override fun updateBlogsLocalBase(currentUser: FirebaseUser, spinner: ProgressBar) {
        db.collection(currentUser.uid).whereEqualTo("deleted", false).get()
            .addOnSuccessListener { result ->
                val disp = Flowable.fromCallable {
                    val blogs = mutableListOf<BlogEntry>()
                    for(doc in result) {
                        val fileName = UUID.randomUUID().toString() + ".body"
                        val outputStreamWriter =
                            OutputStreamWriter(
                                context.openFileOutput(
                                    fileName,
                                    Context.MODE_PRIVATE
                                )
                            )
                        val encryptedBodyString = doc["body"] as String
                        val decryptInputStream = ByteArrayInputStream(
                            Base64Utils.decode(encryptedBodyString)
                        )
                        val decryptOutputStream = ByteArrayOutputStream()
                        encryptionService.decrypt(decryptInputStream, decryptOutputStream)
                        val body = decryptOutputStream.toByteArray().toString(Charsets.UTF_8)
                        outputStreamWriter.use { it.write(body) }
                        val possiblePost = doc.toObject(BlogEntry::class.java)
                        possiblePost.bodyPath = fileName
                        blogs.add(possiblePost)
                    }
                    blogs
                }.flatMapCompletable {
                    blogEntriesRepository.insertAll(it)
                }.compose(RxSchedulers.completableAsync()).subscribe({
                    spinner.visibility = View.GONE
                },{
                    if(it is IOException){
                        Toast.makeText(context, R.string.encryptor_error, Toast.LENGTH_LONG).show()
                    }
                    spinner.visibility = View.GONE
                    Timber.d(it)
                })
            }
    }

    override fun deleteBlogsLocalBase(currentUser: FirebaseUser,lifecycleOwner: LifecycleOwner) {
        val blogEntries = blogEntriesRepository.getBlogEntriesWith(true)
        deleteBlogsObserver = Observer{ deletedBlogs ->
            db.runBatch { batch ->
                deletedBlogs.forEach {
                    val dbReference = db.collection(currentUser.uid).document(it.uid.toString())
                    batch.delete(dbReference)
                }
            }.addOnCompleteListener {
                blogEntriesRepository.deleteAll(deletedBlogs)
                blogEntries.removeObserver(deleteBlogsObserver)
            }
        }
        blogEntries.observe(lifecycleOwner, deleteBlogsObserver)
    }

    override fun syncWithFireBase(spinner : ProgressBar, lifecycleOwner: LifecycleOwner){
        auth.signInAnonymously().addOnSuccessListener {
            val currentUser = it.user
            if(currentUser != null){
                updateBlogsFireBase(currentUser,lifecycleOwner)
                updateBlogsLocalBase(currentUser,spinner)
                deleteBlogsLocalBase(currentUser,lifecycleOwner)
            }
        }.addOnFailureListener {
            Timber.d(it)
        }

    }
}