# 1. محافظت از مدل‌های اطلاعاتی و دیتابیس (بسیار مهم برای Room و Firebase)
-keep class com.hooman.einkaufszettel.domain.model.** { *; }
-keep class com.hooman.einkaufszettel.data.local.entity.** { *; }

# 2. محافظت از کدهای پس‌زمینه (Coroutines)
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# 3. محافظت از کلاس‌های اصلی فایربیس و احراز هویت (محدودتر برای رفع اخطار)
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.FirebaseApp { *; }
-keep class com.google.firebase.FirebaseOptions { *; }

# 4. محافظت از کلاس‌های اصلی ورود با گوگل
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.auth.api.identity.** { *; }
-keep class com.google.android.gms.common.api.** { *; }

# (اختیاری) محافظت از کلاس‌های معماری خودتان در صورت نیاز
-keep class com.hooman.einkaufszettel.feature.presentation.** { *; }
-keep class com.hooman.einkaufszettel.core.presentation.** { *; }