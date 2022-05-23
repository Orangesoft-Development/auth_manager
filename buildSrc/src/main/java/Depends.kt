object Depends {

    object BuildPlugins {
        const val gradlePlugin = "com.android.tools.build:gradle:${Versions.Android.gradlePlugin}"
        const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.Android.mavenGradlePlugin}"
        const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val googleServices = "com.google.gms:google-services:${Versions.googleServices}"
    }

    object Kotlin {
        const val stdlib =  "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val extensions =  "org.jetbrains.kotlin:kotlin-android-extensions-runtime:${Versions.kotlin}"
        const val core = "androidx.core:core-ktx:${Versions.ktxVersion}"
    }

    object BaseAndroid {
        const val material = "com.google.android.material:material:${Versions.material}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
        const val fragments = "androidx.fragment:fragment:${Versions.fragments}"
        const val preference = "androidx.preference:preference:${Versions.preference}"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    }

    object Api {
        const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.logging}"
        const val gson = "com.squareup.retrofit2:converter-gson:${Versions.gson}"
    }

    object Lifecycle {
        const val runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
        const val plugin = "android.arch.lifecycle:compiler:${Versions.lifecycle}"
    }

    object PlayServices {
        const val auth = "com.google.android.gms:play-services-auth:${Versions.playServicesAuth}"
        const val base = "com.google.android.gms:play-services-base:${Versions.playServicesBase}"
        const val coroutinesPlayServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.playServicesCoroutines}"
    }

    object Facebook {
        const val auth = "com.facebook.android:facebook-login:${Versions.facebookAuth}"
    }

    object Firebase {
        const val firebasePlatform = "com.google.firebase:firebase-bom:${Versions.firebasePlatform}"
        const val auth = "com.google.firebase:firebase-auth-ktx"
        const val messaging = "com.google.firebase:firebase-messaging"
    }
}