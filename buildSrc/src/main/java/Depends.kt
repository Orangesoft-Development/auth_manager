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
        const val constraint = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val cardview = "androidx.cardview:cardview:${Versions.cardView}"
        const val annotations = "androidx.annotation:annotation:${Versions.annotations}"
        const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
        const val fragments = "androidx.fragment:fragment:${Versions.fragments}"
    }

    object Paging {
        const val core = "androidx.paging:paging-runtime:${Versions.paging}"
        const val searchable = "com.github.orangesoft-co:searchable_paging:${Versions.searchablePaging}"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    }

    object Database {
        const val runtime = "androidx.room:room-runtime:${Versions.room}"
        const val ktx = "androidx.room:room-ktx:${Versions.room}"
    }

    object DatabasePlugin {
        const val plugin = "androidx.room:room-compiler:${Versions.room}"
    }

    object Api {
        const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.logging}"
        const val gson = "com.squareup.retrofit2:converter-gson:${Versions.gson}"

        const val core = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val kotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
        const val adapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
        const val converter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    }

    object Test {
        const val runner = "androidx.test:runner:${Versions.runner}"
        const val rules = "androidx.test:rules:${Versions.rules}"
        const val junit = "androidx.test.ext:junit:${Versions.junit}"
        const val core_testing = "androidx.test:core:${Versions.core_testing}"
        const val arch_core_testing = "androidx.arch.core:core-testing:${Versions.arch_core_testing}"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    }

    object Lifecycle {
        const val runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
        const val extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
        const val viewModelExtensions = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
        //const val liveDataExtensions = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    }

    object LifecyclePlugins {
        const val plugin = "android.arch.lifecycle:compiler:${Versions.lifecycle}"
    }

    object BindingCollections {
        const val core = "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter:${Versions.bindingCollections}"
        const val paging = "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter-paging:${Versions.bindingCollections}"
        const val plugin = "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter-recyclerview:${Versions.bindingCollections}"
    }

    object PlayServices {
        const val auth = "com.google.android.gms:play-services-auth:${Versions.playServices}"
        const val base = "com.google.android.gms:play-services-base:17.0.0"
    }

    object Facebook {
        const val auth = "com.facebook.android:facebook-login:${Versions.facebookAuth}"
    }

    object Firebase {
        const val auth = "com.google.firebase:firebase-auth:${Versions.firebaseAuth}"
        const val messaging = "com.google.firebase:firebase-messaging:20.2.3"
    }
}