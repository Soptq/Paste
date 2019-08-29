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

#fabric
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

#glide

#recyclerview
-keepclasseswithmembers class android.support.v7.widget.RecyclerView$ViewHolder {
   public final View *;
}
-dontwarn com.yanzhenjie.recyclerview.swipe.**
-keep class com.yanzhenjie.recyclerview.swipe.** {*;}
#litepal
-keep class org.litepal.** {
    *;
}

-keep class * extends org.litepal.crud.DataSupport {
    *;
}

#-dontusemixedcaseclassnames
#
#-dontskipnonpubliclibraryclasses
#-dontpreverify
#
#-verbose

#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod
#
#-renamesourcefileattribute SourceFile
#
#-keepattributes SourceFile,LineNumberTable
#
#-dontnote android.support.**
#-dontwarn android.support.**

#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * entends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * entends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService

#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
#-keepclasseswithmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-keep class * implements android.os.Parcelable{
#    public static final android.os.Parcelable$Creator *;
#}

#-keep class com.github.test.** {*;}
#-keep public class com.jayway.jsonpath.** {*;}
-dontwarn com.jayway.jsonpath.**
#-keep public class org.slf4j.** {*;}
-dontwarn org.slf4j.**

#trpay
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}

#picasso
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

#okhttp3
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

#Google play
-keep class com.android.vending.billing.**
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *;}

-keep class com.blankj.utilcode.** { *; }
-keepclassmembers class com.blankj.utilcode.** { *; }
-dontwarn com.blankj.utilcode.**