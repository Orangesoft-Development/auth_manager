buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Depends.BuildPlugins.gradlePlugin)
        classpath(Depends.BuildPlugins.kotlinPlugin)
        classpath(Depends.BuildPlugins.mavenGradlePlugin)
        classpath("com.google.gms:google-services:4.3.3")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://jitpack.io/")
        }
    }
}