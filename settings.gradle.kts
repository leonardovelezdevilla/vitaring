pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VitaRing"

// app  = aplicación del celular (Android)
// wear = aplicación del reloj (Wear OS, Galaxy Watch4 o más nuevo)
include(":app", ":wear")
