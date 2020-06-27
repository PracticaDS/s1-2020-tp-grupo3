package ar.edu.unq.pdes.myprivateblog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import org.wordpress.aztec.Aztec
import org.wordpress.aztec.AztecText
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.glideloader.GlideImageLoader
import org.wordpress.aztec.glideloader.GlideVideoThumbnailLoader
import org.wordpress.aztec.source.SourceViewEditText
import org.wordpress.aztec.toolbar.AztecToolbar
import org.wordpress.aztec.toolbar.IAztecToolbarClickListener
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {

    abstract val layoutId: Int

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    fun getActivityViewModel(): MainActivityViewModel = (activity as MainActivity).viewModel

    fun getMainActivity(): MainActivity = (activity as MainActivity)

    protected fun applyStatusBarStyle(backgroundColor: Int, lumaThreshold: Float = 0.7f) {
        val window = getMainActivity().window
        val brightness = ColorUtils.luminance(backgroundColor)

        if (brightness > lumaThreshold) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            getMainActivity().toolbar.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = 0 // clear all flags
            getMainActivity().toolbar.systemUiVisibility = 0
        }
        getMainActivity().toolbar.setBackgroundColor(backgroundColor)
        window.statusBarColor = backgroundColor
    }

    fun showError(errorMsg: String){
        val textMsg = errorMsg
        val durationT = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context!!.applicationContext,textMsg,durationT)
        toast.show()
    }

}

object ColorUtils {
    fun findTextColorGivenBackgroundColor(color: Int, lumaThreshold: Float = 0.7f): Int {
        return if (ColorUtils.luminance(color) > lumaThreshold) {
            Color.DKGRAY
        } else {
            Color.WHITE
        }
    }

    fun luminance(@ColorInt color: Int): Float = saturate(
        0.2126f * Color.red(color) / 255f + 0.7152f * Color.green(color) / 255f + 0.0722f * Color.blue(
            color
        ) / 255f
    )

    private fun saturate(v: Float): Float = if (v <= 0.0f) 0.0f else if (v >= 1.0f) 1.0f else v
}

fun Context.setAztec(body: AztecText, source: SourceViewEditText, formatting_toolbar: AztecToolbar) =
    apply {  Aztec.with(body, source, formatting_toolbar, object : IAztecToolbarClickListener {
        override fun onToolbarCollapseButtonClicked() {
        }

        override fun onToolbarExpandButtonClicked() {
        }

        override fun onToolbarFormatButtonClicked(
            format: ITextFormat,
            isKeyboardShortcut: Boolean
        ) {
        }

        override fun onToolbarHeadingButtonClicked() {
        }

        override fun onToolbarHtmlButtonClicked() {
        }

        override fun onToolbarListButtonClicked() {
        }

        override fun onToolbarMediaButtonClicked(): Boolean = false

    })
        .setImageGetter(GlideImageLoader(this))
        .setVideoThumbnailGetter(GlideVideoThumbnailLoader(this))
    }