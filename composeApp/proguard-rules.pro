# ==========================================
# Einkaufszettel ProGuard Rules
# ==========================================

# 1. محافظت از مدل‌های اطلاعاتی (بسیار مهم برای Room و Firebase)
# (اگر پکیج مدل‌های شما جای دیگری است، مسیر زیر را اصلاح کنید)
-keep class com.hooman.einkaufszettel.domain.model.** { *; }
-keep class com.hooman.einkaufszettel.data.local.entity.** { *; }

# 3. محافظت از کدهای پس‌زمینه (Coroutines)
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
