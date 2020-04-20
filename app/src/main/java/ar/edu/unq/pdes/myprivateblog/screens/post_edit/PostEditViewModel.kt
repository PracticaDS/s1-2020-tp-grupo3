package ar.edu.unq.pdes.myprivateblog.screens.post_edit

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import io.reactivex.Flowable
import timber.log.Timber
import java.io.OutputStreamWriter
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.math.absoluteValue


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

    val errorMsg = MutableLiveData("")


    val post = MutableLiveData<BlogEntry?>()

    fun fetchBlogEntry(id: EntityID) {

        val disposable = blogEntriesRepository
            .fetchById(id)
            .compose(RxSchedulers.flowableAsync())
            .subscribe {
                post.value = it
                cardColor.value = it.cardColor
            }
    }

    fun updatePost() {
        val disposable = Flowable.fromCallable {
            if(titleText.value.toString().isBlank()){
                throw IllegalArgumentException()
            }

            val fileName = UUID.randomUUID().toString() + ".body"
            val outputStreamWriter =
                
          StreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.use { it.write(bodyText.value) }
            fileName

        }.flatMapCompletable {
            val colorToUpdate : Int = cardColor.value!!

            blogEntriesRepository.updateBlogEntry(
                BlogEntry(
                    uid = post.value!!.uid,
                    bodyPath = it,
                    title = titleText.value.toString(),
                    cardColor = colorToUpdate
                )
            )

        }.compose(RxSchedulers.completableAsync()).subscribe ({
            state.value = State.SUCCESS
    },{throwable -> state.value = State.ERROR
        Timber.e(throwable)});

    }
}
