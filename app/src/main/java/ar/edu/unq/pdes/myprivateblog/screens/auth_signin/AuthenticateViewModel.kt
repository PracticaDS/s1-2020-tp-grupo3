package ar.edu.unq.pdes.myprivateblog.screens.auth_signin

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.EventTracker
import ar.edu.unq.pdes.myprivateblog.services.AuthenticationService
import com.google.firebase.analytics.FirebaseAnalytics
import java.lang.Exception
import javax.inject.Inject


class AuthenticateViewModel @Inject constructor(
    val context: Context,
    val trackEvents: EventTracker,
    val authService: AuthenticationService
) : ViewModel() {

    val RC_SIGN_IN: Int = 1

    fun loggedIn() = authService.loggedIn()
    fun getIntent() = authService.signInIntent()

    fun login(requestCode: Int, data: Intent?, succCallback: () -> Unit,errCallback: (Exception?) -> Unit) {
        if (requestCode != RC_SIGN_IN) {
            return
        }
        authService.login(data, succCallback, errCallback)
    }

    fun registerLogin(){
        trackEvents.logEvent(FirebaseAnalytics.Event.LOGIN)
    }

    fun registerLoginNoAuth(){
        trackEvents.logEvent("login-no-auth")
    }

    fun registerLoginFailed(){
        trackEvents.logEvent("login-failed")
    }

    fun storePassword(password: String){
        authService.storePassword(password)
    }

}
