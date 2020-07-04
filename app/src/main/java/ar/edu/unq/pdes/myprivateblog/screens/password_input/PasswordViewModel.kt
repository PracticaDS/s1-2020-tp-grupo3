package ar.edu.unq.pdes.myprivateblog.screens.password_input

import android.content.Context
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.EventTracker
import ar.edu.unq.pdes.myprivateblog.services.AuthenticationService
import timber.log.Timber
import javax.inject.Inject

class PasswordViewModel @Inject constructor(
    val context: Context,
    val trackEvents: EventTracker,
    val authService: AuthenticationService
) : ViewModel() {

    fun storePassword(password: String){
        authService.storePassword(password)
    }

    fun storePasswordFailed(){
        trackEvents.logEvent("store-password-failed")
        Timber.d("Falló el guardado del password")
    }

}