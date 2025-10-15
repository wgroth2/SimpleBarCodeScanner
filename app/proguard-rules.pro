# Add project specific ProGuard rules here.
# By default, the build system applies consumer ProGuard rules from library
# dependencies. You don't need to duplicate them here.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If you keep the line number information, uncomment this to
# hide the original source file name.
# -renamesourcefileattribute SourceFile

# Retain debugging information for stack traces.
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Keep all public classes and members that are entry points to the app.
# This includes Activities, Services, BroadcastReceivers, ContentProviders.
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.core.app.CoreComponentFactory
-keep public class * extends androidx.lifecycle.ViewModel

# Keep custom views and their constructors.
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep your app's data classes to prevent issues with serialization (e.g., Gson, Moshi).
# Adjust the package name to match your project's structure if needed.
-keep class com.digiroth.simplebarcodescanner.data.** { *; }

# Most modern libraries (like Compose, Coroutines, Retrofit, Room, etc.)
# include their own Proguard rules which are applied automatically.
# You typically do not need to add rules for them here.
