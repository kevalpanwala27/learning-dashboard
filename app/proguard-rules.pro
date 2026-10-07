# kotlinx.serialization: keep generated serializers for our DTOs.
-keepclassmembers class com.example.learningdashboard.data.remote.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.learningdashboard.data.remote.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.learningdashboard.**$$serializer { *; }

# Tink (used by EncryptedSharedPreferences) references ErrorProne annotations that are compile-only.
-dontwarn com.google.errorprone.annotations.**

# Retrofit keeps generic signatures for suspend functions.
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
