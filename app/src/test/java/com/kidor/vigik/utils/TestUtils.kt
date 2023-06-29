package com.kidor.vigik.utils

private const val TEST_REPORT = "#TEST_REPORT "
/**
 * Label used when logging test message in order to find and extract them for reporting.
 */
const val TEST_REPORT_TAG = TEST_REPORT + "\t\t"

object TestUtils {

    /**
     * Prints test's class and name as they can be add into test report.
     */
    fun logTestName() {
        val currentClassIndex = Thread.currentThread().stackTrace.indexOfFirst { stackTraceElement ->
            stackTraceElement.className.contains(this::class.java.simpleName)
        }
        // Test caller is the next trace
        val callerStackTrace = Thread.currentThread().stackTrace[currentClassIndex + 1]
        println("\n" + """$TEST_REPORT${callerStackTrace.fileName} -> ${callerStackTrace.methodName}()""")
    }
}
