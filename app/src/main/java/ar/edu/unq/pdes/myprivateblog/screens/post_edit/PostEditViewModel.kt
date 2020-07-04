package ar.edu.unq.pdes.myprivateblog.screens.post_edit

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.data.ErrorState
import ar.edu.unq.pdes.myprivateblog.services.PostService
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class PostEditViewModel @Inject constructor(
    val postService: PostService,
    val context: Context
) : ViewModel() {

    val titleText = MutableLiveData("")
    val bodyText = MutableLiveData("")
    val cardColor = MutableLiveData<Int>(Color.LTGRAY)
    val bodyHtml = MutableLiveData("")

    val errors = MutableLiveData<ErrorState?>()

    val post = MutableLiveData<BlogEntry?>()


    fun fetchBlogEntry(id: EntityID) {
        val disp = postService.fetchPost(id).map {
            bodyHtml.value = File(context.filesDir, it.bodyPath).readText()
            it
        }.subscribe({
            post.value = it
            cardColor.value = it.cardColor
        },{
            Timber.d(it)
            errors.value = ErrorState.error(it)
        })
    }


    fun updatePost() {
        if(titleText.value.toString().isBlank()){
            errors.value = ErrorState.validationError()
            Timber.d("Error de validación de post")
            return
        }
        val disp = postService.updatePost(post.value!!.uid,cardColor.value!!,bodyText.value!!,titleText.value!!)
            .subscribe({
                errors.value = null
            },{throwable ->
                Timber.d(throwable)
                errors.value = ErrorState.error(throwable)
            })

    }
}
