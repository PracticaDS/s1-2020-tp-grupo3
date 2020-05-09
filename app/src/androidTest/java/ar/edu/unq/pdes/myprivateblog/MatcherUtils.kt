package ar.edu.unq.pdes.myprivateblog

import android.graphics.drawable.ColorDrawable
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.test.espresso.Root
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class MatcherUtils {


    class ToastMatcher : TypeSafeMatcher<Root?>() {

        override fun describeTo(description: Description?) {
            description?.appendText("is toast")
        }

        override fun matchesSafely(item: Root?): Boolean {
            val type: Int? = item?.windowLayoutParams?.get()?.type
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                val windowToken: IBinder = item.decorView.windowToken
                val appToken: IBinder = item.decorView.applicationWindowToken
                if (windowToken === appToken) { // means this window isn't contained by any other windows.
                    return true
                }
            }
            return false
        }

    }

    companion object{
        fun withBackgroundColor(colorPicked: Int): Matcher<View?> {
            return object : BoundedMatcher<View?, View>(View::class.java) {
                public override fun matchesSafely(view: View): Boolean {
                    val color = view.background.current as ColorDrawable
                    return color.color == colorPicked
                }
                override fun describeTo(description: Description) {
                    description.appendText("Checking the matcher on received view: ")
                    description.appendText("with expectedColor = $colorPicked")
                }
            }
        }


        fun withTintColor(expectedColor: Int): Matcher<View?> {
            return object : BoundedMatcher<View?, View>(View::class.java) {
                public override fun matchesSafely(view: View): Boolean {
                    return view.backgroundTintList?.defaultColor == expectedColor
                }
                override fun describeTo(description: Description) {
                    description.appendText("Checking the matcher on received view: ")
                    description.appendText("with expectedColor = $expectedColor")
                }
            }
        }
    }


}