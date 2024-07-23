pluginManagement {
    repositories {
        google()
        jcenter()
//        maven {
//            url = uri("https://maven.aliyun.com/repository/jcenter")
//        }
        maven {
            url = uri("https://jitpack.io")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        jcenter()
        google()
//        maven {
//            url = uri("https://maven.aliyun.com/repository/jcenter")
//        }
        maven {
            url = uri("https://jitpack.io")
        }
        mavenCentral()
    }
}

rootProject.name = "notex"
include(":app")
