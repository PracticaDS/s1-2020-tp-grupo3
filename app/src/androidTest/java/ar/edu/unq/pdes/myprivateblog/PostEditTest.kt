package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.containsString
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

    }

    @Test
    fun whenTappingOnUpdatePost_postEditionScreenShouldOpen() {

        onView(ViewMatchers.withId(R.id.create_new_post))
            .perform(click())

        onView(ViewMatchers.withId(R.id.title)).perform(clearText())

        onView(ViewMatchers.withId(R.id.title))
            .perform(click(),replaceText("Nuevo post"))

        onView(ViewMatchers.withId(R.id.body))
            .perform(click(),replaceText("Este es un post de prueba"))

        onView(ViewMatchers.withId(R.id.btn_save)).perform(click())

        onView(ViewMatchers.withId(R.id.btn_edit)).perform(click())

        onView(ViewMatchers.withId(R.id.title)).perform(click(),
            replaceText("Nuevo Titulo")
        )

        onView(ViewMatchers.withId(R.id.btn_save)).perform(click())

        onView(ViewMatchers.withId(R.id.title)).check(matches(withText("Nuevo Titulo")))
    }
}