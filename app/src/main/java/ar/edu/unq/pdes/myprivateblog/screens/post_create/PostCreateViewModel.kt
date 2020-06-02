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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("testcollection")

    var post = 0

    fun createPost() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val blogEntry = BlogEntry(
            title = titleText.value.toString(),
            bodyPath = bodyText.value!!,
            cardColor = cardColor.value!!
        )
        val disposable = postService.createPost(cardColor.value!!,
            bodyText.value!!,
            titleText.value.toString()
        ).subscribe({
                post = it.toInt()
                trackEvents.logEvent("post-create")
                errors.value = null
                if(currentUser != null && currentUser.email != null){
                    db.collection(currentUser.email!!).document(it.toString()).set(blogEntry)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener {
                        }
                }

        },{throwable ->
            errors.value = ErrorState.error(throwable)
            Timber.d(throwable)
        })

    }

}
