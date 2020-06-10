package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject

class SynchronizeService @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context){

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun syncWithFireBase(){
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser != null && currentUser.email != null){
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
                    val posts = blogEntriesRepository.getAllBlogEntries()
                    for(document in result){
                        val possiblePost = document.toObject(BlogEntry::class.java)
                        if(posts.value?.all { blogEntry -> blogEntry.uid != possiblePost.uid }!!){
                            blogEntriesRepository.createBlogEntry(possiblePost)
                        }
                    }
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