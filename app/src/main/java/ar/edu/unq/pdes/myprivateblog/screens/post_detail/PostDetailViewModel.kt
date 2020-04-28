package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.screens.post_create.PostCreateViewModel
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_post_detail.*
import timber.log.Timber
import java.io.OutputStreamWriter
import java.util.*
import javax.inject.Inject

class PostDetailViewModel @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context
) : ViewModel() {

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
                BlogEntry(uid = post.value!!.uid))
        }.compose(RxSchedulers.completableAsync()).subscribe({
            deleteOnSuccesToast()
        },{throwable -> deleteOnErrorToast()
            Timber.e(throwable)})
    }

    fun deleteOnErrorToast(){
        val textMsg = "Something went wrong deleting"
        val durationT = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context!!.applicationContext,textMsg,durationT)
        toast.show()
    }

    fun deleteOnSuccesToast(){
        val textMsg = "Post was removed"
        val durationT = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context!!.applicationContext,textMsg,durationT)
        toast.show()
    }
}