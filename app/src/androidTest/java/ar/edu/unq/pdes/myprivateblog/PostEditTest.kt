package ar.edu.unq.pdes.myprivateblog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.di.ApplicationModule.provideAppDatabase
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
@LargeTest
class PostEditTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        //TODO: Tener un post creado para no tener que crear uno en cada flujo
    }

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
}