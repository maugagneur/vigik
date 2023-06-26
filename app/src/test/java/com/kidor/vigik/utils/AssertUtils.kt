package com.kidor.vigik.utils

import org.junit.Assert
import java.text.DateFormat
import java.util.Arrays
import java.util.Date

/**
 * A set of assertion methods useful for writing tests.
 *
 * These methods can be used directly: `Assert.assertEquals(...)`, however, they read better if
 * they are referenced through static import:
 * ```
 * import static com.kidor.vigik.utils.Assert.assertEquals;
 * ...
 * assertEquals(...);
 * ```
 */
object AssertUtils {

    /**
     * Asserts that an object isn't null.
     */
    fun assertNotNull(obj: Any?, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected value for '$objectName': not null, Obtained: $obj")
        Assert.assertNotNull(obj)
    }

    /**
     * Asserts that an object is null.
     */
    fun assertNull(obj: Any?, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected value for '$objectName': null, Obtained: $obj")
        Assert.assertNull(obj)
    }

    /**
     * Asserts that a condition is true.
     */
    fun assertTrue(condition: Boolean?, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected value for '$objectName': true, Obtained: $condition")
        Assert.assertEquals(true, condition)
    }

    /**
     * Asserts that a condition is false.
     */
    fun assertFalse(condition: Boolean?, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected value for '$objectName': false, Obtained: $condition")
        Assert.assertEquals(false, condition)
    }

    /**
     * Asserts that two objects are equal.
     */
    fun assertEquals(expected: Any?, actual: Any?, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected value for '$objectName': $expected, Obtained: $actual")
        Assert.assertEquals(expected, actual)
    }

    /**
     * Asserts that two long are equal.
     */
    fun assertEquals(expected: Long, actual: Long, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected value for '$objectName': $expected, Obtained: $actual")
        Assert.assertEquals(expected, actual)
    }

    /**
     * Asserts that two doubles are equal to within a positive delta.
     */
    fun assertEquals(expected: Double, actual: Double, delta: Double, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected value for '$objectName': $expected, Obtained: $actual")
        Assert.assertEquals(expected, actual, delta)
    }

    /**
     * Asserts that two byte arrays are equal.
     */
    fun assertArrayEquals(expected: ByteArray?, actual: ByteArray?, objectName: String) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        println("$TEST_REPORT_TAG$currentDateTimeString, Expected value for '$objectName': " + Arrays.toString(expected) + ", Obtained: " + Arrays.toString(actual))
        Assert.assertArrayEquals(null, expected, actual)
    }
}
