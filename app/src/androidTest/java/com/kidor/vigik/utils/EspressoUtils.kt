package com.kidor.vigik.utils

import android.app.Activity
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.not
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
        onView(withId(viewId))
            .check(matches(isDisplayed()))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is not visible.
     */
    fun checkViewIsNotVisible(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is not visible")
        onView(withId(viewId))
            .check(matches(not(isDisplayed())))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is visible and displays the expected text.
     */
    fun checkViewIsVisibleWithText(viewId: Int, expected: String, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is visible and contains text: '$expected'")
        onView(withId(viewId))
            .check(matches(isDisplayed()))
            .check(matches(withText(expected)))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is visible and displays the text associated with given resource ID.
     */
    fun checkViewIsVisibleWithText(viewId: Int, @StringRes stringResourceId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is visible and contains text: " + getStringResource(stringResourceId))
        onView(withId(viewId))
            .check(matches(isDisplayed()))
            .check(matches(withText(stringResourceId)))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that a visible TextView displaying the text associated with given resource ID exist in given parent view.
     */
    fun checkTextViewInParentIsVisibleWithText(parentId: Int, resourceId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is instance of TextView and has parent matching with ID $parentId, is visible and contains text: " + getStringResource(resourceId))
        onView(allOf(instanceOf(TextView::class.java), withParent(withId(parentId))))
            .check(matches(isDisplayed()))
            .check(matches(withText(resourceId)))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is checked.
     */
    fun checkViewIsChecked(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is checked")
        onView(withId(viewId))
            .check(matches(ViewMatchers.isChecked()))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that the view is not checked.
     */
    fun checkViewIsNotChecked(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is not checked")
        onView(withId(viewId))
            .check(matches(ViewMatchers.isNotChecked()))
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Checks that a Toast is showing the text associated with given resource ID.
     */
    fun checkToastWithTextIsVisible(activity: Activity?, @StringRes stringResourceId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected '$objectName' is visible and contains text: " + getStringResource(stringResourceId))
        onView(withText(stringResourceId))
            .inRoot(withDecorView(not(activity?.window?.decorView)))
            .check(matches(isDisplayed()))
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
     *
     * Set the parameter `fromRootActivity` to `true` when navigate outside of the application or process under test
     * otherwise it will throw an exception.
     */
    fun performBackPress(fromRootActivity: Boolean = false) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Perform back press (fromRootActivity = $fromRootActivity)")
        if (fromRootActivity) {
            pressBackUnconditionally()
        } else {
            pressBack()
        }
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }

    /**
     * Performs click on the view.
     */
    fun performClickOnView(viewId: Int, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Perform click on '$objectName'")
        onView(withId(viewId))
            .perform(click())
        println("$TEST_REPORT_TAG$currentDateTimeString, Result -> OK")
    }
}
