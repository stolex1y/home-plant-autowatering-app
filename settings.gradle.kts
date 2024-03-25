pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        jcenter()
    }
}

rootProject.name = "Home plant autowatering"

include(":app")
include(":domain:model")
include(":domain:repository")
include(":data:local")
include(":data:remote")
include(":ui:common")
include(":ui:widgets")
include(":core:common")
include(":core:common-test")
