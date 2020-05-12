package ar.edu.unq.pdes.myprivateblog.screens.post_create

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.ErrorState
import ar.edu.unq.pdes.myprivateblog.data.EventTracker
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.services.PostService
import io.reactivex.Flowable
import timber.log.Timber
import java.io.OutputStreamWriter
import java.lang.Exception
import java.util.*
import javax.inject.Inject


class PostCreateViewModel @Inject constructor(
    val postService: PostService,
    val context: Context,
    val trackEvents: EventTracker
) : ViewModel() {


    val errors = MutableLiveData<ErrorState?>()
    val titleText = MutableLiveData("")
    val bodyText = MutableLiveData("")
    val cardColor = MutableLiveData<Int>(Color.LTGRAY)

    var post = 0

    fun createPost() {
        val disposable = postService.createPost(cardColor.value!!,
            bodyText.value!!,
            titleText.value.toString()
        ).subscribe({
                post = it.toInt()
                trackEvents.logEvent("post-create")
                errors.value = null
        },{throwable ->
            errors.value = ErrorState.error(throwable)
            Timber.d(throwable)
        })

    }

}
