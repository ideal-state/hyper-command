rootProject.name = "hyper-command"

include(":api")
include(":impl")

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            name = "aliyun-public"
            url = uri("https://maven.aliyun.com/repository/public")
        }
        maven {
            name = "sonatype-releases"
            url = uri("https://oss.sonatype.org/content/repositories/releases/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

buildscript {
    repositories {
        mavenLocal()
        maven {
            name = "aliyun-public"
            url = uri("https://maven.aliyun.com/repository/public")
        }
        maven {
            name = "sonatype-releases"
            url = uri("https://oss.sonatype.org/content/repositories/releases/")
        }
        mavenCentral()
    }
    dependencies {
        classpath("org.apache.commons:commons-lang3:3.14.0")
    }
}
