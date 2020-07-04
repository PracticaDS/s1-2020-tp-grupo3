package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.ColorUtils
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import kotlinx.android.synthetic.main.fragment_post_detail.*
import java.io.File
import android.widget.Toast
import ar.edu.unq.pdes.myprivateblog.data.ErrorState
import ar.edu.unq.pdes.myprivateblog.utils.longToast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_post_detail.body
import kotlinx.android.synthetic.main.fragment_post_detail.header_background
import kotlinx.android.synthetic.main.fragment_post_detail.title
import kotlinx.android.synthetic.main.fragment_post_edit.*

class PostDetailFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_post_detail

    private val viewModel by viewModels<PostDetailViewModel> { viewModelFactory }

    private val args: PostDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchBlogEntry(args.postId)

        viewModel.post.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                renderBlogEntry(it)
            }
        })
        viewModel.errors.observe(viewLifecycleOwner, Observer {
            if(it != null){
                renderError(it)

            }
            else{
                context?.longToast(getString(R.string.succes_msg_post_was_removed))
                findNavController().navigateUp()
            }


        })

        btn_back.setOnClickListener {
            findNavController().navigateUp()
        }

        btn_edit.setOnClickListener {
            findNavController().navigate(PostDetailFragmentDirections.navActionEditPost(args.postId))
        }

        btn_delete.setOnClickListener {
            viewModel.deletePost()
            Snackbar.make(it, R.string.succes_msg_post_was_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_action) {
                    viewModel.undoDelete()
                }.show();
        }
    }

    fun renderBlogEntry(post: BlogEntry) {
        title.text = post.title

        header_background.setBackgroundColor(post.cardColor)
        applyStatusBarStyle(post.cardColor)
        title.setTextColor(ColorUtils.findTextColorGivenBackgroundColor(post.cardColor))

        body.settings.javaScriptEnabled = true
        body.settings.setAppCacheEnabled(true)
        body.settings.cacheMode = WebSettings.LOAD_DEFAULT
        body.webViewClient = WebViewClient()
        if (post.bodyPath != null && context != null) {
            body.loadData(viewModel.bodyHtml.value, "text/html", "UTF-8")
        }
    }

    private fun renderError(errorState: ErrorState){
        showError(errorState.getErrorMessage())
    }
}
