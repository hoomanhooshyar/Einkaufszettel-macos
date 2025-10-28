import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
    alias(libs.plugins.cocoapods)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

//    targets.withType<KotlinNativeTarget>().configureEach {
//        compilations.getByName("main").cinterops.create("FirebaseAnalyticsIosArm64") {
//            val isSimulator = name.contains("Simulator", ignoreCase = true)
//            compilerOpts(
//                "-fmodules",
//                "-fcxx-modules",
//                "-isysroot",
//                "/Users/hooman/sdk/iPhoneOS17.4.sdk"
//            )
//
//        }
//    }


    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    cocoapods{
        version = "1.0.0"
        summary = "Compose Multiplatform Einkaufszettel App"
        homepage = "https://github.com/hooman/einkaufszettel"
        ios.deploymentTarget = "14.0"
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }



        pod("GoogleSignIn") {
            extraOpts += listOf("-compiler-options", "-fmodules -fcxx-modules")
        }
        pod("FirebaseCore"){
            extraOpts += listOf("-compiler-options", "-fmodules -fcxx-modules")
        }
        pod("FirebaseAuth"){
            extraOpts += listOf("-compiler-options", "-fmodules -fcxx-modules")
        }
        pod("FirebaseFirestore"){
            extraOpts += listOf("-compiler-options", "-fmodules -fcxx-modules")
        }
        pod("FirebaseAnalytics"){
            extraOpts += listOf("-compiler-options", "-fmodules -fcxx-modules")
        }
        extraSpecAttributes["source_files"] = "'../iosApp/GoogleSignInBridge.{h,m}'"

    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(project.dependencies.platform(libs.firebase.bom.get()))
            implementation(libs.firebase.analytics.get())
            implementation(libs.firebase.auth.ktx)
            implementation(libs.firebase.firestore.ktx)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.compose.icons.core)
            implementation(libs.compose.icons.extended)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.core)

            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)

            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)

            implementation(libs.kotlinx.datetime)



        }
        nativeMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }




}

android {
    namespace = "com.hooman.einkaufszettel"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.hooman.einkaufszettel"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    ksp(libs.androidx.room.compiler.get())
    add("kspAndroid", libs.androidx.room.compiler.get())
    add("kspIosX64", libs.androidx.room.compiler.get())
    add("kspIosArm64", libs.androidx.room.compiler.get())
    add("kspIosSimulatorArm64", libs.androidx.room.compiler.get())
    implementation(libs.google.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    debugImplementation(compose.uiTooling)
}