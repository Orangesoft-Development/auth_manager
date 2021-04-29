plugins {
    id("com.android.application")

    /**Uncomment this line and add your own google-services json to the 'app' folder**/
    id("com.google.gms.google-services")

    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

kapt {
    correctErrorTypes = true
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
        applicationId = Config.Application.appId
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
        versionCode = Config.Application.versionCode
        versionName = Config.Application.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {

        getByName("debug") {
            isDebuggable = true
        }

        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":auth"))
    implementation(project(":firebase"))
    implementation(project(":apple"))
    implementation(project(":google"))
    implementation(project(":facebook"))
    implementation(project(":phone"))

    implementation(Depends.Kotlin.stdlib)
    implementation(Depends.Kotlin.core)
    implementation(Depends.Kotlin.extensions)

    implementation(Depends.BaseAndroid.material)
    implementation(Depends.BaseAndroid.appcompat)

    implementation(Depends.Coroutines.core)
    implementation(Depends.Coroutines.android)

    implementation(Depends.Api.okhttp)
    implementation(Depends.Api.retrofit)
    implementation(Depends.Api.logging)
    implementation(Depends.Api.gson)

    implementation(Depends.Lifecycle.runtime)
    kapt(Depends.Lifecycle.plugin)

    implementation(Depends.PlayServices.base)
    implementation(Depends.PlayServices.auth)
    implementation(Depends.PlayServices.coroutinesPlayServices)
    implementation(Depends.Facebook.auth)

    implementation(platform(Depends.Firebase.firebasePlatform))
    implementation(Depends.Firebase.auth)
    implementation(Depends.Firebase.messaging)
    implementation("androidx.preference:preference:1.1.1")
}