package com.kidor.vigik.utils

import android.app.Activity
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers
import java.text.DateFormat
import java.util.Date

/**
 * A set of assertion methods based on Espresso useful for writing instrumentation tests.
 *
 * These methods can be used directly: `EspressoUtils.checkViewIsVisible(...)`, however, they read
 * better if they are referenced through static import:
 * ```
 * import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisible;
 *      ...
 *      checkViewIsVisible(...);
 * ```
 */
object EspressoUtils {

    /**
     * Checks that the view is visible.
     */
    fun checkViewIsVisible(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is visible")
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is not visible.
     */
    fun checkViewIsNotVisible(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is not visible")
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is visible and displays the expected text.
     */
    fun checkViewIsVisibleWithText(viewId: Int, expected: String, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is visible and contains text: '$expected'")
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(expected)))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is visible and displays the text associated with given resource ID.
     */
    fun checkViewIsVisibleWithText(viewId: Int, @StringRes stringResourceId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is visible and contains text: " + getStringResource(stringResourceId))
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(stringResourceId)))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that a visible TextView displaying the text associated with given resource ID
     * exist in given parent view.
     */
    fun checkTextViewInParentIsVisibleWithText(parentId: Int, resourceId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is instance of TextView and has parent matching with ID $parentId, is visible and contains text: " + getStringResource(resourceId))
        Espresso.onView(
            CoreMatchers.allOf(
                CoreMatchers.instanceOf(
                    TextView::class.java
                ), ViewMatchers.withParent(ViewMatchers.withId(parentId))
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(resourceId)))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is checked.
     */
    fun checkViewIsChecked(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is checked")
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is not checked.
     */
    fun checkViewIsNotChecked(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is not checked")
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that a Toast is showing the text associated with given resource ID.
     */
    fun checkToastWithTextIsVisible(activity: Activity?, @StringRes stringResourceId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is visible and contains text: " + getStringResource(stringResourceId))
        Espresso.onView(ViewMatchers.withText(stringResourceId))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(activity?.window?.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Returns a localized string from the application's package's default string table.
     */
    private fun getStringResource(resourceId: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(resourceId)
    }

    /**
     * Performs a press on the back button.
     * <pre>
     * Set the parameter `fromRootActivity` to `true` when navigate outside of the application or
     * process under test otherwise it will throw an exception.
     * </pre> *
     */
    @JvmOverloads
    fun performBackPress(fromRootActivity: Boolean = false) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Perform back press (fromRootActivity = $fromRootActivity)")
        if (fromRootActivity) {
            Espresso.pressBackUnconditionally()
        } else {
            Espresso.pressBack()
        }
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Performs click on the view.
     */
    fun performClickOnView(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Perform click on '$objectName'")
        Espresso.onView(ViewMatchers.withId(viewId))
            .perform(ViewActions.click())
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }
}