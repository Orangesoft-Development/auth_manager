buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Depends.BuildPlugins.gradlePlugin)
        classpath(Depends.BuildPlugins.kotlinPlugin)
        classpath(Depends.BuildPlugins.mavenGradlePlugin)
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