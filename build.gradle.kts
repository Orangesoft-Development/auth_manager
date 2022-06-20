buildscript {

    val kotlin_version by extra("1.6.10")
    repositories {
        google()
        jcenter()
        maven(url = "https://developer.huawei.com/repo/")
    }
    dependencies {
        classpath(Depends.BuildPlugins.gradlePlugin)
        classpath(Depends.BuildPlugins.kotlinPlugin)
        classpath(Depends.BuildPlugins.mavenGradlePlugin)
        classpath(Depends.BuildPlugins.googleServices)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath(Depends.Huawei.agConnectAGCP)
    }
}

allprojects {
    repositories {
        google()
        maven {
            setUrl("https://jitpack.io/")
        }
        jcenter()

        maven(url = "https://developer.huawei.com/repo/")
    }
}