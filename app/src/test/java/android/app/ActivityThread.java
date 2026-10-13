package android.app;

/**
 * Stub class for local JVM unit tests.
 *
 * <p><b>DO NOT DELETE THIS CLASS</b>, even though it appears unused and is not directly referenced
 * anywhere in the project's source code.</p>
 *
 * <h3>Why this class is required:</h3>
 * <ul>
 *   <li>AndroidX Glance's {@code androidx.glance.appwidget.GlanceAppWidgetManager} attempts to resolve
 *       the current process name during its companion object's static initialization
 *       ({@code GlanceAppWidgetManager.Companion.getProcessName()}).</li>
 *   <li>It does this dynamically via reflection:
 *       <pre>{@code
 *       Class.forName("android.app.ActivityThread")
 *           .getMethod("currentProcessName")
 *           .invoke(null)
 *       }</pre></li>
 *   <li>In a real Android runtime, {@code android.app.ActivityThread} is a hidden framework class provided
 *       by the OS. However, in local JVM unit tests (running with standard JUnit and MockK without Robolectric),
 *       the stubbed Android SDK jar ({@code android.jar}) does not contain this internal class.</li>
 *   <li>Deleting this stub causes tests that touch {@code GlanceAppWidgetManager} (such as
 *       {@code com.kidor.vigik.ui.widget.ApesWidgetWorkerTest}) to fail with:
 *       <pre>{@code java.lang.ClassNotFoundException: android.app.ActivityThread}</pre>
 *       which wraps into an {@link java.lang.ExceptionInInitializerError}.</li>
 * </ul>
 */
public class ActivityThread {
    public static String currentProcessName() {
        return "com.kidor.vigik";
    }
}
