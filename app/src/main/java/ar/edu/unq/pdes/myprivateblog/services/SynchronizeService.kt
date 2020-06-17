package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions
import timber.log.Timber
import javax.inject.Inject

class SynchronizeService @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context){

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun syncWithFireBase(){
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser != null){
            blogEntriesRepository.getBlogEntriesWith(false).observeForever { unsyncBlogs ->
                db.runBatch { batch ->
                    unsyncBlogs.forEach {
                        val dbReference = db.collection(currentUser.uid).document(it.uid.toString())

                        batch.set(dbReference, it, SetOptions.merge())
                    }
                }.addOnSuccessListener {
                    unsyncBlogs.forEach {
                        it.synced = true
                        blogEntriesRepository.updateBlogEntry(it)
                    }
                }
            }

            db.collection(currentUser.uid).whereEqualTo("deleted", false).get()
                .addOnSuccessListener { result ->
                    val blogsToInsert = mutableListOf<BlogEntry>()
                    for(document in result){
                        val possiblePost = document.toObject(BlogEntry::class.java)
                        blogsToInsert.add(possiblePost)
                    }
                    blogEntriesRepository.insertAll(blogsToInsert).subscribe({Timber.d("TODO OK")},{throwable: Throwable? ->  Timber.d(throwable)})
                }
            blogEntriesRepository.getBlogEntriesWith(true).observeForever { deletedBlogs ->
                db.runBatch { batch ->
                    deletedBlogs.forEach {
                        val dbReference = db.collection(currentUser.uid).document(it.uid.toString())
                        batch.delete(dbReference)
                    }
                }
            }
        }
    }
}