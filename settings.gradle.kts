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

rootProject.name = "JsonQuizzz"

include(":app")
include(":core:design-system")
include(":core:common")
include(":core:testing")
include(":domain")
include(":data")
include(":feature:quiz-player")
include(":feature:quiz-create")
include(":feature:library")
include(":feature:analytics")
include(":feature:sharing")
include(":feature:leaderboard")
include(":feature:prompt-builder")
include(":feature:auth")
