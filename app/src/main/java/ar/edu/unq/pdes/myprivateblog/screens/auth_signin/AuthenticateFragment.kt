package ar.edu.unq.pdes.myprivateblog.screens.auth_signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.ColorUtils
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.data.ErrorState
import ar.edu.unq.pdes.myprivateblog.screens.auth_signin.AuthenticateViewModel
import ar.edu.unq.pdes.myprivateblog.screens.post_edit.PostEditFragmentDirections
import ar.edu.unq.pdes.myprivateblog.setAztec
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_post_edit.*
import kotlinx.android.synthetic.main.fragment_signin.*
import org.wordpress.aztec.Aztec
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.glideloader.GlideImageLoader
import org.wordpress.aztec.glideloader.GlideVideoThumbnailLoader
import org.wordpress.aztec.toolbar.IAztecToolbarClickListener
import timber.log.Timber

class AuthenticateFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_signin

    lateinit var firebaseAuth: FirebaseAuth

    private val viewModel by viewModels<AuthenticateViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser != null){
            findNavController().navigate(
                AuthenticateFragmentDirections.actionAuthenticateFragmentToPostsListingFragment()
            )
        }
        else {
            viewModel.configureGoogleSignIn()
            setupUI()
        }
    }



    private fun setupUI() {
        google_button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = viewModel.mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, viewModel.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == viewModel.RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(getMainActivity(), "Google sign in failed:(" + e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.registerLogin()
                findNavController().navigate(
                    AuthenticateFragmentDirections.actionAuthenticateFragmentToPostsListingFragment()
                )
            } else {
                viewModel.registerLoginFailed()
                Toast.makeText(getMainActivity(), "Google sign in failed:(" + it.result.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


}