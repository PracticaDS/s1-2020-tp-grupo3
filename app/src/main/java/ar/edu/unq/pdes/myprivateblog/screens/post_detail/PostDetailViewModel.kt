package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.data.EventTracker
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import javax.inject.Inject

class PostDetailViewModel @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context,
    val trackEvents: EventTracker
) : ViewModel() {

    var post = MutableLiveData<BlogEntry?>()

    fun fetchBlogEntry(id: EntityID) {

        val disposable = blogEntriesRepository
            .fetchById(id)
            .compose(RxSchedulers.flowableAsync())
            .subscribe {
                post.value = it
                trackEvents.logEvent("post-detail")
            }
    }
}