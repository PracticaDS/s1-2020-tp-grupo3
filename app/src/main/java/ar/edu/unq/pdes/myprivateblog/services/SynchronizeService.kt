package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions
import io.reactivex.Flowable
import timber.log.Timber
import java.io.File
import java.io.OutputStreamWriter
import java.util.*
import javax.inject.Inject

class SynchronizeService @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context,
    val postService: PostService){

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun updateBlogsFireBase(currentUser: FirebaseUser) {
        blogEntriesRepository.getBlogEntriesWith(false, false).observeForever { unsyncBlogs ->
            db.runBatch { batch ->
                unsyncBlogs.forEach {
                    val path = it.bodyPath
                    var body : String = ""
                    if(path != null) {
                        body = File(context.filesDir, path).readText()
                    }
                    val dbReference = db.collection(currentUser.uid).document(it.uid.toString())
                    val firebasePost = object {
                        var uid = it.uid
                        var title = it.title
                        var body = body
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
            }.addOnFailureListener {
                Timber.d(it)
            }
        }
    }

    fun updateBlogsLocalBase(currentUser: FirebaseUser, spinner: ProgressBar) {
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
                        outputStreamWriter.use { it.write(doc["body"] as String) }
                        val possiblePost = doc.toObject(BlogEntry::class.java)
                        possiblePost.bodyPath = fileName
                        blogs.add(possiblePost)
                    }
                    blogs
                }.flatMapCompletable {

                    blogEntriesRepository.insertAll(it)

                }.compose(RxSchedulers.completableAsync()).subscribe({
                    spinner.visibility = View.GONE
                    Timber.d("todo bien")
                },{Timber.d(it)})
            }
    }

    fun deleteBlogsLocalBase(currentUser: FirebaseUser) {
        blogEntriesRepository.getBlogEntriesWith(true).observeForever { deletedBlogs ->
            db.runBatch { batch ->
                deletedBlogs.forEach {
                    val dbReference = db.collection(currentUser.uid).document(it.uid.toString())
                    batch.delete(dbReference)
                }
            }.addOnCompleteListener {
                blogEntriesRepository.deleteAll(deletedBlogs)
            }
        }
    }

    fun syncWithFireBase(spinner : ProgressBar){
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateBlogsFireBase(currentUser)
            updateBlogsLocalBase(currentUser,spinner)
            deleteBlogsLocalBase(currentUser)
        }
    }
}