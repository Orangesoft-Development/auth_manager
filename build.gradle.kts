buildscript {

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Depends.BuildPlugins.gradlePlugin)
        classpath(Depends.BuildPlugins.kotlinPlugin)
        classpath(Depends.BuildPlugins.mavenGradlePlugin)
        classpath(Depends.BuildPlugins.googleServices)
    }
}

allprojects {
    repositories {
        google()
        maven {
            setUrl("https://jitpack.io/")
        }
        jcenter()
    }
}