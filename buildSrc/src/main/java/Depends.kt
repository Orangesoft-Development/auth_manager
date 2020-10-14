object Depends {

    object BuildPlugins {
        const val gradlePlugin = "com.android.tools.build:gradle:${Versions.Android.gradlePlugin}"
        const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.Android.mavenGradlePlugin}"
        const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val navigationPlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
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
        const val cardview = "androidx.cardview:cardview:1.0.0"
        const val annotations = "androidx.annotation:annotation:1.1.0"
        const val localBroadcast = "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"
        const val fragmentsKtx = "androidx.fragment:fragment-ktx:${Versions.fragments}"
        const val fragments = "androidx.fragment:fragment:${Versions.fragments}"
        const val transition = "androidx.transition:transition:${Versions.transition}"
        const val swipeToRefresh = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeToRefresh}"
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

    object Navigation {
        const val fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
        const val ui = "androidx.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
    }

    object Lifecycle {
        const val runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
        const val extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
        const val viewModelExtensions = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
        const val liveDataExtensions = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    }

    object LifecyclePlugins {
        const val plugin = "android.arch.lifecycle:compiler:${Versions.lifecycle}"
    }

    object Image {
        const val core = "com.github.bumptech.glide:glide:${Versions.glide}"
        const val glideOkHttp =  "com.github.bumptech.glide:okhttp-integration:${Versions.glide}"
        const val plugin = "com.caverock:androidsvg:1.4"
    }

    object ImagePlugins {
        const val compiler =  "com.github.bumptech.glide:compiler:${Versions.glide}"
    }

    object BindingCollections {
        const val core = "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter:${Versions.bindingCollections}"
        const val paging = "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter-paging:${Versions.bindingCollections}"
        const val viewPager = "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter-viewpager2:${Versions.bindingCollections}"
        const val plugin = "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter-recyclerview:${Versions.bindingCollections}"
    }

    object Di {
        const val core = "com.google.dagger:dagger:${Versions.di}"
        const val support = "com.google.dagger:dagger-android-support:${Versions.di}"
    }

    object DiPlugins {
        const val compiler = "com.google.dagger:dagger-compiler:${Versions.di}"
        const val processor = "com.google.dagger:dagger-android-processor:${Versions.di}"
    }

    object Misc {
        const val discreteScrollView = "com.yarolegovich:discrete-scrollview:1.4.9"
        const val charts = "com.github.PhilJay:MPAndroidChart:v3.1.0"
        const val easyPopUps = "com.github.zyyoona7:EasyPopup:1.1.2"
        const val viewPagerIndicator = "com.github.adrielcafe:PageIndicatorView:1.0.6"
        const val imageViewer = "com.github.piasy:BigImageViewer:1.6.5"
        const val imageViewerGlideSupport = "com.github.piasy:GlideImageLoader:1.6.5"
    }

    object Analytics {
        const val firebase = "com.google.firebase:firebase-analytics:${Versions.firebaseAnalytics}"
        const val crashlytics = "com.google.firebase:firebase-crashlytics:${Versions.crashlytics}"
    }

    object PlayServices {
        const val auth = "com.google.android.gms:play-services-auth:${Versions.playServices}"
        const val base = "com.google.android.gms:play-services-base:17.0.0"
    }

    const val swipeLayout = "com.daimajia.swipelayout:library:${Versions.swipeLayout}"

    const val pinEntryEditText = "com.alimuzaffar.lib:pinentryedittext:${Versions.pinEntry}"

    object Handy {
        const val core = "com.github.trueddd.handy:core:${Versions.handy}"
        const val databinding = "com.github.trueddd.handy:databinding:${Versions.handy}"
    }

    object Facebook {
        const val auth = "com.facebook.android:facebook-login:${Versions.facebookAuth}"
    }

    object Firebase {
        const val auth = "com.google.firebase:firebase-auth:${Versions.firebaseAuth}"
        const val messaging = "com.google.firebase:firebase-messaging:20.2.3"
    }

    object Worker {
        const val worker = "androidx.work:work-runtime:${Versions.worker}"
        const val workerKtx = "androidx.work:work-runtime-ktx:${Versions.worker}"
    }

    const val inputMask = "com.redmadrobot:input-mask-android:${Versions.inputMask}"

    object Exif {
        const val picture = "androidx.exifinterface:exifinterface:1.2.0"
        const val video = "org.jcodec:jcodec:0.2.3"
        const val videoAndroid = "org.jcodec:jcodec-android:0.2.3"
    }

    const val lokalise = "com.lokalise.android:sdk:${Versions.lokalise}"

    const val rangeSeekBar = "com.github.Innovattic:range-seek-bar:v1.0.6"

    const val lottie = "com.airbnb.android:lottie:3.4.1"

    const val multidex = "androidx.multidex:multidex:2.0.1"
}