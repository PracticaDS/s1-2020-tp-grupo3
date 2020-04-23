package ar.edu.unq.pdes.myprivateblog.screens.post_edit

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.ColorUtils
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import androidx.lifecycle.Observer
import ar.edu.unq.pdes.myprivateblog.setAztec
import kotlinx.android.synthetic.main.fragment_post_edit.*
import timber.log.Timber

class PostEditFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_post_edit

    private val viewModel by viewModels<PostEditViewModel> { viewModelFactory }

    private val args: PostEditFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchBlogEntry(args.postId)

        viewModel.post.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                renderBlogEntry(it)
            }
        })

        viewModel.bodyHtml.observe(viewLifecycleOwner, Observer {
            if (it != null){
                body.fromHtml(it)
            }
        })



        viewModel.errors.observe(viewLifecycleOwner, Observer {
            if(it != null){
                renderError(it)
            }
            else{
                viewModel.post.value?.let {post -> PostEditFragmentDirections.navActionUpdatePost(post.uid) }?.let {
                        id ->
                                findNavController().navigate(
                                    id
                                )
                }
            }

        })


        btn_close.setOnClickListener {
            closeAndGoBack()
        }



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
            viewModel.updatePost()
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

    private fun renderBlogEntry(post: BlogEntry) {
        title.setText(post.title)
        header_background.setBackgroundColor(post.cardColor)
        applyStatusBarStyle(post.cardColor)
        title.setTextColor(ColorUtils.findTextColorGivenBackgroundColor(post.cardColor))
    }

    private fun closeAndGoBack() {
        findNavController().navigateUp()
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

    private fun renderError(errorState: ErrorState){
        title.isEnabled = true
        body.isEnabled = true
        showError(errorState.getErrorMessage())
        if(errorState.getType() == ErrorState.ErrorType.VALIDATION){
            title.error = resources.getString(R.string.post_without_title)
            btn_save.isEnabled = false
        }
    }
}