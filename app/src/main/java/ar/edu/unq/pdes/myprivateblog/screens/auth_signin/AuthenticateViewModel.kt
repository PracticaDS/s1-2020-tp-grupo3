package ar.edu.unq.pdes.myprivateblog.screens.auth_signin

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
import io.reactivex.Flowable
import timber.log.Timber
import java.io.OutputStreamWriter
import java.lang.Exception
import java.util.*
import javax.inject.Inject


class AuthenticateViewModel @Inject constructor(
    val context: Context,
    val trackEvents: EventTracker
) : ViewModel() {






}
