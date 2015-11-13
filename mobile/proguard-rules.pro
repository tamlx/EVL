# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

#-injars      bin/classes
#-injars      libs
#-outjars     bin/classes-processed.jar
#-libraryjars libs
#-libraryjars D:\Android\adt\sdk\platforms\android-19\android.jar

-dontshrink
-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep class javax.** { *; }
-keep class org.** { *; }
-keep class twitter4j.** { *; }

# for ndk
-keepclasseswithmembernames class * {
    native <methods>;
}
#  keep tts
-keep class com.acapelagroup.android.tts.acattsandroid { *; }

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Processing serializable classes
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class **.R$*
-keep class **.R
-keepattributes InnerClasses

# Skip resource
-adaptresourcefilenames    **.properties,**.data
#-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

# To return error line number
# Producing useful obfuscated stack traces
-printmapping out.map
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# for google oauth lib
-keep class com.google.api.client.** { *; }
-keep class com.google.api.services.calendar.model.** { *; }

# Keep libraries
-keep class android.support.v4.app.** { *; } 
-keep interface android.support.v4.app.** { *; } 
-keep class android.support.v7.app.** { *; } 
-keep interface android.support.v7.app.** { *; } 

-keep class com.viewpagerindicator.** {*;}
-keep class org.apache.http.** { *; }
-keep class nostra13.universalimageloader.** { *; }

-keep class com.facebook.** { *; }
-keepattributes Signature

# Kepp all classes for google play service
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

# For google analytics.
-keep public class com.google.** {*;}

# For Google Play Service
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

# Needed by google-api-client to keep generic types and @Key annotations accessed via reflection

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault