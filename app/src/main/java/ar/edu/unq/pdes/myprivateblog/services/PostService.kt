package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import java.io.OutputStreamWriter
import java.util.*
import javax.inject.Inject

class PostService @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context){


    fun fetchPost(id: EntityID) : Flowable<BlogEntry>{
        return blogEntriesRepository.fetchById(id).compose(RxSchedulers.flowableAsync())
    }

    fun updatePost(id: EntityID, cardColor : Int, body: String, title : String) : Completable{

        return convertBody(body).flatMapCompletable {
            blogEntriesRepository.updateBlogEntry(
                BlogEntry(
                    uid = id,
                    bodyPath = it,
                    title = title,
                    cardColor = cardColor
                )
            )
        }.compose(RxSchedulers.completableAsync())
    }

    private fun convertBody(body : String) : Flowable<String>{
        return Flowable.fromCallable {
            val fileName = UUID.randomUUID().toString() + ".body"
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.use { it.write(body) }
            fileName

        }
    }

    fun deletePost(post: BlogEntry) : Completable{
        return blogEntriesRepository.updateBlogEntry(
                BlogEntry(
                    uid = post.uid,
                    bodyPath = post.bodyPath,
                    title = post.title,
                    cardColor = post.cardColor,
                    deleted = true
                )
        ).compose(RxSchedulers.completableAsync())

    }

    fun createPost(cardColor : Int, body: String, title : String) : Flowable<Long>{
        return convertBody(body).flatMapSingle {
            blogEntriesRepository.createBlogEntry(
                BlogEntry(
                    title = title,
                    bodyPath = it,
                    cardColor = cardColor
                )
            )

        }.compose(RxSchedulers.flowableAsync())
    }
}