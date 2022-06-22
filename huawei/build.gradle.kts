plugins {
    id("com.android.library")
    id("com.github.dcendents.android-maven")
    kotlin("android")
    kotlin("kapt")
}

group = "com.github.orangesoft-co.auth_manager"
version = "1.0.0"

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
    implementation(project(":firebase"))
    implementation(Depends.Kotlin.stdlib)
    implementation(Depends.Kotlin.core)

    implementation(Depends.Coroutines.core)
    implementation(Depends.Coroutines.android)

    implementation(Depends.Huawei.agConnectCore)
    implementation(Depends.Huawei.agConnectAuth)

    implementation(Depends.Firebase.auth)

    implementation(Depends.Api.gson)

}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets["main"].java.srcDirs)
    }

    artifacts {
        archives(sourcesJar)
    }
}