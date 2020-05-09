package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.data.EventTracker
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.screens.post_create.PostCreateViewModel
import ar.edu.unq.pdes.myprivateblog.utils.SimpleErrorMessage
import ar.edu.unq.pdes.myprivateblog.utils.SimpleSuccesMessage
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_post_detail.*
import timber.log.Timber
import java.io.OutputStreamWriter
import java.util.*
import javax.inject.Inject

class PostDetailViewModel @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context,
    val trackEvents: EventTracker
) : ViewModel() {

    var post = MutableLiveData<BlogEntry?>()
    var errorMessage = MutableLiveData<SimpleErrorMessage?>()
    var succesMessage = MutableLiveData<SimpleSuccesMessage?>()

    fun fetchBlogEntry(id: EntityID) {
        val disposable = blogEntriesRepository
            .fetchById(id)
            .compose(RxSchedulers.flowableAsync())
            .subscribe {
                post.value = it
                trackEvents.logEvent("post-detail")
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
                BlogEntry(uid = post.value!!.uid))
        }.compose(RxSchedulers.completableAsync()).subscribe({
            succesMessage.postValue(SimpleSuccesMessage(R.string.succes_msg_post_was_removed))
        },{throwable ->
            errorMessage.value = SimpleErrorMessage(R.string.error_msg_something_went_wrong_deleting)
            Timber.e(throwable)})
    }

}

