package ar.edu.unq.pdes.myprivateblog.screens.post_create

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
import ar.edu.unq.pdes.myprivateblog.screens.post_edit.PostEditFragmentDirections
import ar.edu.unq.pdes.myprivateblog.setAztec
import kotlinx.android.synthetic.main.fragment_post_edit.*
import org.wordpress.aztec.Aztec
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.glideloader.GlideImageLoader
import org.wordpress.aztec.glideloader.GlideVideoThumbnailLoader
import org.wordpress.aztec.toolbar.IAztecToolbarClickListener
import timber.log.Timber

class PostCreateFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_post_edit

    private val viewModel by viewModels<PostCreateViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.errors.observe(viewLifecycleOwner, Observer {
            if(it != null){
                renderError(it)
            }
            else{
                if(viewModel.post != 0) {

                    findNavController().navigate(
                        PostCreateFragmentDirections.navActionSaveNewPost(viewModel.post)
                    )
                }
            }


        })

        viewModel.cardColor.observe(viewLifecycleOwner, Observer {
            renderHeaderColor(it)
        })

        title.doOnTextChanged { text, start, count, after ->
            viewModel.titleText.postValue(text.toString())
        }

        body.doOnTextChanged { text, start, count, after ->
            viewModel.bodyText.value = body.toFormattedHtml()
            Timber.d(viewModel.bodyText.value)
        }

        btn_save.setOnClickListener {
            viewModel.createPost()
        }

        btn_close.setOnClickListener {
            closeAndGoBack()
        }

        color_picker.onColorSelectionListener = {
            viewModel.cardColor.postValue(it)
        }

        context?.apply {
            this.setAztec(body, source, formatting_toolbar)
        }


    }

    private fun renderError(errorState: ErrorState){
        title.isEnabled = true
        body.isEnabled = true
        showError(errorState.getErrorMessage())
        if(errorState.getType() == ErrorState.ErrorType.VALIDATION){
            title.error = resources.getString(R.string.post_without_title)
            btn_save.isEnabled = false
        }
    }

    private fun renderHeaderColor(color:Int){
        header_background.setBackgroundColor(color)
        val itemsColor = ColorUtils.findTextColorGivenBackgroundColor(color)
        title.setTextColor(itemsColor)
        title.setHintTextColor(itemsColor)
        btn_save.setColorFilter(itemsColor)
        btn_close.setColorFilter(itemsColor)

        applyStatusBarStyle(color)
    }

    private fun closeAndGoBack() {
        findNavController().navigateUp()
    }
}