package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.screens.post_create.PostCreateViewModel
import io.reactivex.Flowable
import java.io.OutputStreamWriter
import java.util.*
import javax.inject.Inject

class PostDetailViewModel @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context
) : ViewModel() {

    enum class State {
        DELETING, SUCCESS, ERROR,
    }

    val state = MutableLiveData(State.DELETING)

    var post = MutableLiveData<BlogEntry?>()

    fun fetchBlogEntry(id: EntityID) {

        val disposable = blogEntriesRepository
            .fetchById(id)
            .compose(RxSchedulers.flowableAsync())
            .subscribe {
                post.value = it
            }
    }

    fun deletePost(){

        val disposable = Flowable.fromCallable {

            val fileName = UUID.randomUUID().toString() + ".body"
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.use { it.write(post.value!!.uid) }
            fileName

        }.flatMapCompletable  {

            blogEntriesRepository.deleteBlogEntry(
                BlogEntry(
                    uid = post.value!!.uid
                ))

        }.compose(RxSchedulers.completableAsync()).subscribe {
            state.value = State.SUCCESS
        }


    }
}