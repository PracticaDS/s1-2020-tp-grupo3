package ar.edu.unq.pdes.myprivateblog.screens.password_input

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.R
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_password.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class PasswordFragment: BaseFragment() {
    override val layoutId = R.layout.fragment_password

    private val viewModel by viewModels<PasswordViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupFloatingLabelError()
        save_password.setOnClickListener {
            storePassword()
        }
    }

    private fun goToPostListing(){
        findNavController().navigate(
            PasswordFragmentDirections.actionPasswordFragmentToPostsListingFragment()
        )
    }

    private fun getPassowrd(): String {
        val editText: EditText = getMainActivity().findViewById(R.id.password)
        return editText.text.toString()
    }

    private fun storePassword(){
        try {
            viewModel.storePassword(getPassowrd())
            goToPostListing()
        }
        catch (e: ApiException){
            showToast(getText(R.string.password_store_fail).toString())
            viewModel.storePasswordFailed()
        }
    }

    fun isValidPassword(): Boolean {
        val password = getPassowrd()
        return ((password.length > 7) && passwordCharValidation(password))
    }

    fun passwordCharValidation(passwordEd: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).*$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher: Matcher = pattern.matcher(passwordEd)
        return matcher.matches()
    }

    private fun setupFloatingLabelError() {
        val floatingUsernameLabel: TextInputLayout = getMainActivity().findViewById(R.id.password_text_input_layout)
        val saveButton: Button = getMainActivity().findViewById(R.id.save_password)
        floatingUsernameLabel.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                text: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                if (isValidPassword()) {
                    floatingUsernameLabel.isErrorEnabled = false
                    saveButton.isEnabled = true
                } else {
                    floatingUsernameLabel.error = getString(R.string.password_requirements)
                    floatingUsernameLabel.isErrorEnabled = true
                    saveButton.isEnabled = false
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(getMainActivity(), message, Toast.LENGTH_SHORT).show()
    }
}