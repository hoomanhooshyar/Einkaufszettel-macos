 # --- محافظت از کل ساختار و پکیج‌های پروژه خودتان ---
-keep class com.hooman.einkaufszettel.** { *; }
-keep class com.hooman.einkaufszettel.domain.** { *; }
-keep class com.hooman.einkaufszettel.data.** { *; }
-keep class com.hooman.einkaufszettel.feature.** { *; }

# --- Jetpack Compose & UI ---
-keep class androidx.compose.** { *; }
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# --- Room Database ---
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# --- محافظت صددرصدی از تمام انوتیشن‌ها و کلاس‌های Room ---
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod

-keepclassmembers class * {
    @androidx.room.Query <methods>;
    @androidx.room.Insert <methods>;
    @androidx.room.Update <methods>;
    @androidx.room.Delete <methods>;
    @androidx.room.Transaction <methods>;
}

-keepclassmembers class * extends androidx.room.RoomDatabase {
    public *;
}

# --- Koin (Dependency Injection) ---
-keep class org.koin.** { *; }
-keepnames class * {
    @org.koin.core.annotation.* <fields>;
}

# --- Firebase & Google Play Services ---
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

-keep @**.KeepForFirebase class * { *; }
-keepclassmembers @**.KeepForFirebase class * { *; }

-keep class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers enum * { *; }