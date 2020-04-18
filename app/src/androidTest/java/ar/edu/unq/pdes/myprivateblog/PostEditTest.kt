package ar.edu.unq.pdes.myprivateblog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.Is.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.coroutineContext


@RunWith(AndroidJUnit4::class)
@LargeTest
class PostEditTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun whenTappingOnUpdatePost_postEditionScreenShouldOpen() {

        onView(withId(R.id.create_new_post))
            .perform(click())

        onView(withId(R.id.title)).perform(clearText())

        onView(withId(R.id.title))
            .perform(click(),replaceText("Nuevo post"))

        onView(withId(R.id.body))
            .perform(click(),replaceText("Este es un post de prueba"))

        onView(withId(R.id.btn_save)).perform(click())

        onView(withId(R.id.btn_edit)).perform(click())

        onView(withId(R.id.title)).check(matches(withText("Nuevo post")))

        val colorPicked = Color.LTGRAY;
        onView(withId(R.id.header_background)).check(matches(withBackgroundColor(colorPicked)))


    }



    @Test
    fun whenEditingPost_postFieldsInPostDetailsViewShouldHaveChanged() {

        onView(withId(R.id.create_new_post))
            .perform(click())

        onView(withId(R.id.title)).perform(clearText())

        onView(withId(R.id.title))
            .perform(click(),replaceText("Nuevo post"))

        onView(withId(R.id.body))
            .perform(click(),replaceText("Este es un post de prueba"))

        onView(withId(R.id.btn_save)).perform(click())

        onView(withId(R.id.btn_edit)).perform(click())

        onView(withId(R.id.title)).perform(click(),
            replaceText("Nuevo Titulo")
        )

        val colorToPick = Color.parseColor("#b39ddb")

        onView(withTintColor(colorToPick)).perform(click())

        onView(withId(R.id.btn_save)).perform(click())

        onView(withId(R.id.title)).check(matches(withText("Nuevo Titulo")))

        onView(withId(R.id.header_background)).check(matches(withBackgroundColor(colorToPick)))
    }


    @Test
    fun whenEditingPostWithNoTitle_shouldShowAnError() {

        onView(withId(R.id.create_new_post))
            .perform(click())

        onView(withId(R.id.title)).perform(clearText())

        onView(withId(R.id.title))
            .perform(click(),replaceText("Nuevo post"))

        onView(withId(R.id.body))
            .perform(click(),replaceText("Este es un post de prueba"))

        onView(withId(R.id.btn_save)).perform(click())

        onView(withId(R.id.btn_edit)).perform(click())

        onView(withId(R.id.title)).perform(click(),
            replaceText("")
        )

        onView(withId(R.id.btn_save)).perform(click())

        onView(withId(R.id.title)).check(matches(hasErrorText("Debe tener algún título")))

        onView(withText("Error al guardar el post")).inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    private fun withBackgroundColor(colorPicked: Int): Matcher<View?> {
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


    private fun withTintColor(expectedColor: Int): Matcher<View?> {
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
}