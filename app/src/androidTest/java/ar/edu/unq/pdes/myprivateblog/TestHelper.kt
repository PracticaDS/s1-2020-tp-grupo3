package ar.edu.unq.pdes.myprivateblog

import android.graphics.Color
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers.not

fun postCreation(){
    Espresso.onView(ViewMatchers.withId(R.id.create_new_post))
        .perform(ViewActions.click())

    val postTitle = "post1"

    Espresso.onView(ViewMatchers.withId(R.id.title))
        .perform(ViewActions.typeText(postTitle))

    val bodyText = "This is the body"
    Espresso.onView(ViewMatchers.withId(R.id.body))
        .perform(ViewActions.typeText(bodyText))

    val pickedColor = Color.parseColor("#b39ddb")

    Espresso.onView(MatcherUtils.withTintColor(pickedColor))
        .perform(ViewActions.click())

    Espresso.onView(ViewMatchers.withId(R.id.btn_save))
        .perform(ViewActions.click())

    Espresso.onView(ViewMatchers.withId(R.id.title))
        .check(ViewAssertions.matches(ViewMatchers.withText(postTitle)))
}

fun goBack() = Espresso.onView(ViewMatchers.withId(R.id.btn_back)).perform(ViewActions.click())

fun writePassword(){
    Espresso.onView(ViewMatchers.withId(R.id.save_password))
        .check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))

    Espresso.onView(ViewMatchers.withId(R.id.password))
        .perform(ViewActions.replaceText("goodPASSWORD123"))

    Espresso.onView(ViewMatchers.withId(R.id.password))
        .perform(ViewActions.closeSoftKeyboard())

    Espresso.onView(ViewMatchers.withId(R.id.save_password))
        .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

    Espresso.onView(ViewMatchers.withId(R.id.save_password))
        .perform(ViewActions.click())
}