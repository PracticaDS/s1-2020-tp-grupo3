package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.content.Intent
import ar.edu.unq.pdes.myprivateblog.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

interface AuthenticationService {
    fun currentUser(): FirebaseUser?
    fun loggedIn(): Boolean
    fun signOut()
    fun signInIntent(): Intent?
    fun login(data: Intent?, succCallback: () -> Unit, errCallback: (Exception?) -> Unit)
    fun logout()
    fun addAuthStateListener(listener : FirebaseAuth.AuthStateListener)
    fun removeAuthStateListener(listener : FirebaseAuth.AuthStateListener)
}

class FirebaseAuthService @Inject constructor(context: Context) : AuthenticationService {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail().build()
    private val googleClient = GoogleSignIn.getClient(context, googleConf)
    private val encryptionService = EncryptionService(context)
    override fun currentUser() = firebaseAuth.currentUser

    override fun loggedIn() = currentUser() != null

    override fun signOut() {
        firebaseAuth.signOut()
        googleClient.signOut()
    }

    override fun signInIntent() = googleClient.signInIntent

    override fun login(
        data: Intent?,
        succCallback: () -> Unit,
        errCallback: (Exception?) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            checkOrCreateSecretKey()
            val token = task.getResult(ApiException::class.java)?.idToken ?: return
            val credential = GoogleAuthProvider.getCredential(token, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) succCallback() else errCallback(it.exception)
                }
        } catch (e: ApiException) {
            errCallback(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun addAuthStateListener(listener : FirebaseAuth.AuthStateListener) {
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun removeAuthStateListener(listener : FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(listener)
    }

    private fun checkOrCreateSecretKey() {
        val secretKey = encryptionService.retrieveSecretKey()
        //TODO: guardar secret key en firebase si no existe
        if (secretKey == null) {
            val newSecretKey = encryptionService.generateSecretKey()!!
            //TODO: Guardar secrey key en firebase
        } else {
            encryptionService.storeSecretKey(secretKey)
        }
    }
}

class MockAuthService : AuthenticationService {
    override fun currentUser(): FirebaseUser? = null
    override fun loggedIn() = true
    override fun signOut() {}
    override fun signInIntent(): Intent? = null
    override fun login(data: Intent?, succCallback: () -> Unit, errCallback: (Exception?) -> Unit) {}
    override fun logout() {}
    override fun addAuthStateListener(listener : FirebaseAuth.AuthStateListener) {}
    override fun removeAuthStateListener(listener : FirebaseAuth.AuthStateListener){}
}

