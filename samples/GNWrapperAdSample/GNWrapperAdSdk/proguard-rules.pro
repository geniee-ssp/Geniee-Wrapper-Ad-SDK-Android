# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# main
-keep public class jp.co.geniee.gnwrapperadsdk.GNWrapperAdSDK {
    public *;
}

# banner
-keep public class jp.co.geniee.gnwrapperadsdk.banner.GNWrapperAdBanner {
    public *;
}

# enums
-keep public class jp.co.geniee.gnwrapperadsdk.enums.GNErrorCode {
    public *;
}

-keep public class jp.co.geniee.gnwrapperadsdk.enums.GNLogLevel {
    public *;
}

# listeners
-keep public class jp.co.geniee.gnwrapperadsdk.listeners.GNWrapperAdBannerListener {
    public *;
}

# loggers
-keep public class jp.co.geniee.gnwrapperadsdk.loggers.Log {
    public *;
}
