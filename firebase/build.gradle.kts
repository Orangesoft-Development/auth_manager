plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    buildToolsVersion = Versions.Android.buildTools
    compileSdkVersion(Versions.Android.compileSdk)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
        versionCode = Config.Application.versionCode
        versionName = Config.Application.versionName
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":auth"))

    implementation(Depends.Kotlin.stdlib)
    implementation(Depends.Kotlin.core)

    implementation(Depends.Coroutines.core)
    implementation(Depends.Coroutines.android)

    implementation(Depends.PlayServices.base)
    implementation(Depends.PlayServices.auth)
    implementation(Depends.Facebook.auth)
    implementation(Depends.Firebase.auth)
    implementation(Depends.Firebase.messaging)
}