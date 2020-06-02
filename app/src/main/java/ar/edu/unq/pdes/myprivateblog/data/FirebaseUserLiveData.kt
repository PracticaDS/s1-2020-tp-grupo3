package ar.edu.unq.pdes.myprivateblog.data

import androidx.lifecycle.LiveData
import ar.edu.unq.pdes.myprivateblog.services.AuthenticationService
import ar.edu.unq.pdes.myprivateblog.services.FirebaseAuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * This class observes the current FirebaseUser. If there is no logged in user, FirebaseUser will
 * be null.
 * */
class FirebaseUserLiveData  @Inject constructor(authService: AuthenticationService) : LiveData<FirebaseUser?>() {

    // Firebase auth
    private val firebaseAuth = authService

    /**
     * Firebase auth state listener
     * */
    private val authStateListener = FirebaseAuth.AuthStateListener {
        value = it.currentUser
    }

    /**
     * Add [FirebaseAuth.AuthStateListener] when active
     * */
    override fun onActive() {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    /**
     * Remove [FirebaseAuth.AuthStateListener] when inactive
     * */
    override fun onInactive() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}