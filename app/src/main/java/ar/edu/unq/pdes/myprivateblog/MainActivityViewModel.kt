package ar.edu.unq.pdes.myprivateblog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import ar.edu.unq.pdes.myprivateblog.data.FirebaseUserLiveData
import ar.edu.unq.pdes.myprivateblog.screens.auth_signin.AuthenticateViewModel
import ar.edu.unq.pdes.myprivateblog.services.AuthenticationService
import ar.edu.unq.pdes.myprivateblog.services.FirebaseAuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class MainActivityViewModel  @Inject constructor(
    val authService : AuthenticationService/* add injectable dependencies here */
) : ViewModel() {

    val authenticated : MutableLiveData<FirebaseUser?> = MutableLiveData(null)

    val authStateListener = FirebaseAuth.AuthStateListener {
        authenticated.value = it.currentUser
    }

    fun signOut(){
        authService.signOut()
    }

}