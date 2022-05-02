# Check environment variable
if [ "$ADB_PATH" = '' ]
then
	echo "Please set your ADB_PATH environment variable"
	exit
fi

# Clear build directory
./gradlew clean

# Clear logcat
$ADB_PATH/adb logcat -c

# Store log
$ADB_PATH/adb logcat > .androidTestLog.raw &
logPid=$!

# Launch tests:
#	* test					-> debug and release unit tests
# 	* connectedAndroidTest	-> instrumentation tests
#	* jacocoTestReport		-> merge code coverage results
./gradlew test connectedAndroidTest jacocoTestReport --continue

# Kill the logcat process
kill -9 $logPid

# Move the log file with the JaCoCo test report
mv .androidTestLog.raw app/build/reports/customJacocoReport/jacocoTestReport/html/

# Filter the logs to keep only the interestiong ones
grep -E 'TEST_REPORT|TestRunner' app/build/reports/customJacocoReport/jacocoTestReport/html/.androidTestLog.raw > app/build/reports/customJacocoReport/jacocoTestReport/html/androidTestLog

# Run Kotlin static code analysers
./gradlew detekt
# TODO: Run diktat

echo
echo "Unit tests report is available in app/build/reports/tests"
echo "Instrumentation tests report is available in app/build/reports/androidTests/connected"
echo "Code coverage report is available in app/build/reports/jacocoTestReport/html"
echo "Detekt analysis report is available in app/build/reports/detekt"
