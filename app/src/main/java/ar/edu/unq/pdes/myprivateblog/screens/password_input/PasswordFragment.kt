package ar.edu.unq.pdes.myprivateblog.screens.password_input

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.R
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.fragment_password.*

class PasswordFragment: BaseFragment() {
    override val layoutId = R.layout.fragment_password

    private val viewModel by viewModels<PasswordViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*TODO: encontrar una manera de identificar si el usuario ya estaba logeado,
                solo en este caso no pedir input de password (si existe en el storage local)
        */
//        if (!viewModel.authService.retrievePassword().isNullOrEmpty()) {
//            goToPostListing()
//        } else {
            save_password.setOnClickListener {
                storePassword()
            }
//        }
    }

    private fun goToPostListing(){
        findNavController().navigate(
            PasswordFragmentDirections.actionPasswordFragmentToPostsListingFragment()
        )
    }

    private fun storePassword(){
        try {
            val editText: EditText = getMainActivity().findViewById(R.id.password)
            val password: String = editText.text.toString()
            viewModel.storePassword(password)
            goToPostListing()
        }
        catch (e: ApiException){
            showToast(getText(R.string.password_store_fail).toString())
            viewModel.storePasswordFailed()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getMainActivity(), message, Toast.LENGTH_SHORT).show()
    }
}