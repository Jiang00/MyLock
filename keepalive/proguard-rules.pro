# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/guoyang/Developer/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.android.kpa.NativeDaemonBase{*;}
-keep class com.android.kpa.nativ.NativeDaemonAPI20{*;}
-keep class com.android.kpa.nativ.NativeDaemonAPI21{*;}
-keep class com.android.kpa.DaemonApplication{*;}
-keep class com.android.kpa.DaemonClient{*;}
-keepattributes Exceptions,InnerClasses,...
-keep class com.android.kpa.DaemonConfigurations{*;}
-keep class com.android.kpa.DaemonConfigurations$*{*;}