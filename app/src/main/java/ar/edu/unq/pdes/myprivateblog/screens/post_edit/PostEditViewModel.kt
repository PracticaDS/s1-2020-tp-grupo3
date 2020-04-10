package ar.edu.unq.pdes.myprivateblog.screens.post_edit

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import javax.inject.Inject


class PostEditViewModel @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context
) : ViewModel() {
    enum class State {
        EDITING, SUCCESS, ERROR
    }

    val state = MutableLiveData(State.EDITING)
    val titleText = MutableLiveData("")
    val bodyText = MutableLiveData("")
    val cardColor = MutableLiveData<Int>(Color.LTGRAY)

    var post = MutableLiveData<BlogEntry?>()

    fun fetchBlogEntry(id: EntityID) {

        val disposable = blogEntriesRepository
            .fetchById(id)
            .compose(RxSchedulers.flowableAsync())
            .subscribe {
                post.value = it
            }
    }

    fun updatePost() {
        //TODO

    }
}
