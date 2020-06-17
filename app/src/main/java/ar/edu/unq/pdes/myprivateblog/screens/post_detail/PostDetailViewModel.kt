package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.data.*
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.screens.post_create.PostCreateViewModel
import ar.edu.unq.pdes.myprivateblog.services.PostService
import ar.edu.unq.pdes.myprivateblog.utils.SimpleErrorMessage
import ar.edu.unq.pdes.myprivateblog.utils.SimpleSuccesMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_post_detail.*
import timber.log.Timber
import java.io.File
import java.io.OutputStreamWriter
import java.util.*
import javax.inject.Inject

class PostDetailViewModel @Inject constructor(
    val postService: PostService,
    val context: Context,
    val trackEvents: EventTracker
) : ViewModel() {

    var post = MutableLiveData<BlogEntry?>()
    val bodyHtml = MutableLiveData("")
    val errors = MutableLiveData<ErrorState?>()
    var errorMessage = MutableLiveData<SimpleErrorMessage?>()
    var succesMessage = MutableLiveData<SimpleSuccesMessage?>()
    val db = FirebaseFirestore.getInstance()

    fun fetchBlogEntry(id: EntityID) {
            val disp = postService.fetchPost(id).map {
                bodyHtml.value = File(context.filesDir, it.bodyPath).readText()
                it
            }.subscribe({
                Timber.d("OBTENGO POST")
                post.value = it
            },{Timber.d(it)})


    }

    fun deletePost(){
        val disposable = postService.deletePost(post.value!!).subscribe({
            errors.value = null
        },{throwable -> errors.value = ErrorState.error(throwable)
        })

    }

}

