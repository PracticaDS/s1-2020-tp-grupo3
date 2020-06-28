package ar.edu.unq.pdes.myprivateblog.screens.auth_signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.screens.password_input.PasswordFragment
import ar.edu.unq.pdes.myprivateblog.screens.password_input.PasswordFragmentDirections
import kotlinx.android.synthetic.main.fragment_signin.*

class AuthenticateFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_signin

    private val viewModel by viewModels<AuthenticateViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (viewModel.loggedIn()) {
            goToPasswordInput()
        } else {
            google_button.setOnClickListener {
                login()
            }
        }

        without_auth.setOnClickListener {
            goToPostListing()
            viewModel.registerLoginNoAuth()
        }
    }

    private fun login() {
        startActivityForResult(viewModel.getIntent(), viewModel.RC_SIGN_IN)
    }

    private fun goToPostListing(){
        findNavController().navigate(
            AuthenticateFragmentDirections.actionAuthenticateFragmentToPostsListingFragment()
        )
    }

    private fun goToPasswordInput(){
        findNavController().navigate(
            AuthenticateFragmentDirections.actionAuthenticateFragmentToPasswordFragment()
        )
    }

    override fun onActivityResult(code: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(code, resultCode, data)
        viewModel.login(code,data,
            {
                goToPasswordInput()
                viewModel.registerLogin()
            },
            {
                showToast(getText(R.string.auth_failed).toString())
                viewModel.registerLoginFailed()
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(getMainActivity(), message, Toast.LENGTH_SHORT).show()
    }

}