plugins {
    id("com.android.application")

    /**Uncomment this line and add your own google-services json to the 'app' folder**/
    //id("com.google.gms.google-services")

    kotlin("android")
    kotlin("kapt")

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

    implementation(Depends.Paging.core)

    api(Depends.Database.runtime)
    implementation(Depends.Database.ktx)
    kapt(Depends.DatabasePlugin.plugin)

    androidTestImplementation(Depends.Test.runner)
    androidTestImplementation(Depends.Test.rules)
    androidTestImplementation(Depends.Test.junit)
    androidTestImplementation(Depends.Test.core_testing)
    androidTestImplementation(Depends.Test.arch_core_testing)
    androidTestImplementation(Depends.Test.espresso)

    implementation(Depends.BaseAndroid.constraint)
    implementation(Depends.BaseAndroid.cardview)
    implementation(Depends.BaseAndroid.annotations)

    implementation(Depends.BindingCollections.core)
    implementation(Depends.BindingCollections.plugin)

    implementation(Depends.Lifecycle.runtime)
    implementation(Depends.Lifecycle.extensions)
    implementation(Depends.Lifecycle.viewModelExtensions)
    kapt(Depends.LifecyclePlugins.plugin)

    implementation(Depends.Paging.searchable)

    implementation(Depends.Api.core)
    implementation(Depends.Api.converter)
    implementation(Depends.Api.kotlin)
    implementation(Depends.Api.adapters)
    kapt(Depends.Api.codegen)

    implementation(Depends.PlayServices.base)
    implementation(Depends.PlayServices.auth)
    implementation(Depends.Facebook.auth)
    implementation(Depends.Firebase.auth)
    implementation(Depends.Firebase.messaging)
    implementation(Depends.Firebase.coroutinesPlayServices)
}