package ar.edu.unq.pdes.myprivateblog

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import ar.edu.unq.pdes.myprivateblog.MatcherUtils.Companion.withTintColor
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class PostsListingTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun toolbarIsVisibleWhenThereAreNoPosts() {
        onView(withId(R.id.app_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun toolbarIsVisibleWhenThereArePosts() {
        postCreation()
        goBack()
        onView(withId(R.id.app_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenTappingOnNewPostFab_postCreationScreenShouldOpen() {
        onView(withId(R.id.create_new_post))
            .perform(click())


        onView(withId(R.id.title))
            .check(matches(withHint(R.string.hint_post_title)))
    }

    @Test
    fun whenCreatingPost_shouldNavigateToPostDetail() {
        onView(withId(R.id.create_new_post))
            .perform(click())

        val postTitle = "post1"

        onView(withId(R.id.title))
            .perform(typeText(postTitle))

        val bodyText = "This is the body"
        onView(withId(R.id.body))
            .perform(typeText(bodyText))

        val pickedColor = Color.parseColor("#b39ddb")

        onView(withTintColor(pickedColor))
            .perform(click())

        onView(withId(R.id.btn_save))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btn_save))
            .perform(click())


        onView(withId(R.id.title))
            .check(matches(withText(postTitle)))

//        onView(withId(R.id.body)).check(
//            matches(withWebViewTextMatcher(bodyText))
//        )
    }

    @Test
    fun whenThereAreNoPosts_PostsListingHasAVisibleBackground(){
        onView(withId(R.id.posts_list_recyclerview))
            .check(CustomMatchers.hasItemCount(0))

        onView(withId(R.id.empty_concept_illustration_l))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun whenThereArePosts_PostsListingHasAnInvisibleBackground(){
        postCreation()
        onView(withId(R.id.btn_back)).perform(click())

        onView(withId(R.id.empty_concept_illustration_l))
            .check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun whenTappingOnNewPost_ShouldCreatePostAndShouldAppearInList() {
        onView(withId(R.id.create_new_post))
            .perform(click())

        onView(withId(R.id.title)).perform(clearText())

        onView(withId(R.id.title))
            .perform(click(), ViewActions.replaceText("Nuevo test post"))

        onView(withId(R.id.btn_save))
            .perform(click())

        onView(withId(R.id.btn_back))
            .perform(click())

        //tenemos que integrar commit ari para que se pueda correr test tranqui sino este onView por ej rompe
        onView(withId(R.id.posts_list_recyclerview))
            .check(CustomMatchers.hasItemCount(1))

        onView(withId(R.id.posts_list_recyclerview))
            .check(matches(CustomMatchers.atPosition(0, hasDescendant(withText("Nuevo test post")))))

        val colorPicked = Color.LTGRAY;

        onView(withId(R.id.posts_list_recyclerview))
            .check(matches(CustomMatchers.atPosition(0, hasDescendant(CustomMatchers.withTintColor(colorPicked)))))
    }

    @Test
    fun whenTappingOnNewPost_ShouldCreatePostAndShouldBeAbleToOpenIt() {
        onView(withId(R.id.create_new_post))
            .perform(click())

        onView(withId(R.id.title)).perform(clearText())

        onView(withId(R.id.title))
            .perform(click(), ViewActions.replaceText("Nuevo test post"))

        onView(withId(R.id.btn_save))
            .perform(click())

        onView(withId(R.id.btn_back))
            .perform(click())

        onView(withId(R.id.posts_list_recyclerview))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.title))
            .check(matches(withText("Nuevo test post")))
    }
}
//
//fun withWebViewTextMatcher(expectedText: String): Matcher<View?>? {
//    return object : BoundedMatcher<View?, WebView>(WebView::class.java) {
//
//        override fun describeTo(description: Description) {
//            description.appendText("Checking the matcher on received view: ")
//            description.appendText("with expectedStatus=$expectedText")
//        }
//
//        override fun matchesSafely(webView: View): Boolean {
//            val webViewBody: String = runBlocking {
//                suspendCoroutine<String> { cont ->
//                    webView.evaluateJavascript(
//                        "(function() { return document.documentElement.innerText; })();"
//                    ) {
//                        cont.resume(it)
//                    }
//                }
//            }
//
//            webView.backgroundTintList?.defaultColor
//
//
//            val expected = "\"" + expectedText + "\""
//            return expected == webViewBody
//        }
//
//        suspend fun fetchWebViewContent(webView: WebView): String = suspendCoroutine { cont ->
//            webView.evaluateJavascript(
//                "(function() { return document.documentElement.innerText; })();"
//            ) {
//                cont.resume(it)
//            }
//        }
//
//    }
//}