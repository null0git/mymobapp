# JsonQuizzz ProGuard Rules

# Keep Gson models
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.jsonquizzz.data.model.** { *; }
-keep class com.jsonquizzz.domain.model.** { *; }

# Keep Room entities
-keep class com.jsonquizzz.data.local.entity.** { *; }

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
