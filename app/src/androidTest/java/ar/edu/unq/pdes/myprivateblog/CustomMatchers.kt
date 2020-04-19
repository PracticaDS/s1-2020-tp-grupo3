package ar.edu.unq.pdes.myprivateblog

import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher


object CustomMatchers {

    fun withTintColor(expectedColor: Int): Matcher<View?>? {
        return object : BoundedMatcher<View?, View>(View::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("Checking the matcher on received view: ")
                description.appendText("with expectedStatus=$expectedColor")
            }

            override fun matchesSafely(view: View): Boolean {
                return view.backgroundTintList?.defaultColor == expectedColor
            }
        }
    }

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

    fun hasItemCount(count: Int): ViewAssertion {
        return RecyclerViewItemCountAssertion(count)
    }

    fun atPosition(position: Int, @NonNull itemMatcher: Matcher<View?>): Matcher<View?>? {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position) ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    private class RecyclerViewItemCountAssertion(private val count: Int) : ViewAssertion {

        override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            if (view !is RecyclerView) {
                throw IllegalStateException("The asserted view is not RecyclerView")
            }

            if (view.adapter == null) {
                throw IllegalStateException("No adapter is assigned to RecyclerView")
            }

            ViewMatchers.assertThat("RecyclerView item count", view.adapter!!.itemCount, CoreMatchers.equalTo(count))
        }
    }
}