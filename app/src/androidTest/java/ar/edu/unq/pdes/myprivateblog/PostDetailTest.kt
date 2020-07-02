package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PostDetailTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun whenTapingOnDeletePostButton_postShouldBeRemoved(){
        writePassword()
        postCreation()

        Espresso.onView(ViewMatchers.withId(R.id.btn_delete))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.btn_delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.create_new_post))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.posts_list_recyclerview))
            .check(CustomMatchers.hasItemCount(0))
    }

}