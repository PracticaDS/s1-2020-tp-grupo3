package ar.edu.unq.pdes.myprivateblog.screens.post_create

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EventTracker
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import io.reactivex.Flowable
import timber.log.Timber
import java.io.OutputStreamWriter
import java.lang.Exception
import java.util.*
import javax.inject.Inject


class PostCreateViewModel @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context,
    val trackEvents: EventTracker
) : ViewModel() {

    enum class State {
        EDITING, SUCCESS, ERROR
    }


    val state = MutableLiveData(State.EDITING)
    val titleText = MutableLiveData("")
    val bodyText = MutableLiveData("")
    val cardColor = MutableLiveData<Int>(Color.LTGRAY)

    var post = 0

    fun createPost() {
        // TODO: extract this to some BlogEntryService or BlogEntryActions or some other super meaningful name...
        val disposable = Flowable.fromCallable {
            if(titleText.value.toString().isBlank()){
                throw IllegalArgumentException("title is blank")
            }
            val fileName = UUID.randomUUID().toString() + ".body"
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.use { it.write(bodyText.value) }
            fileName

        }.flatMapSingle {

            blogEntriesRepository.createBlogEntry(
                BlogEntry(
                    title = titleText.value.toString(),
                    bodyPath = it,
                    cardColor = cardColor.value!!
                )
            )

        }.compose(RxSchedulers.flowableAsync()).subscribe ({
            post = it.toInt()
            state.value = State.SUCCESS
            trackEvents.logEvent("post-create")
        },{t -> Timber.e(t)
            state.value = State.ERROR
        })

    }

}
