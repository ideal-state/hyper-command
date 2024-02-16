import org.apache.commons.lang3.time.DateFormatUtils
import java.util.*

plugins {
    id("maven-publish")
    id("signing")
}

val projectName = project.ext["projectName"] as String
val authors = project.ext["authors"] as String
val javaVersion = project.ext["javaVersion"] as Int
val charset = project.ext["charset"] as String

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly(fileTree("${projectDir}/libraries"))

    api("team.idealstate.hyper:hyper-commons-base:1.0.0")

    testCompileOnly("org.jetbrains:annotations:24.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = sourceCompatibility
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

tasks.compileJava {
    options.encoding = charset
    finalizedBy(tasks.test)
}

tasks.processResources {
    filteringCharset = charset
    includeEmptyDirs = false
    val assetsDir = "assets/${projectName}"
    eachFile {
        if (path.startsWith("assets/")) {
            print("$path >> ")
            path = assetsDir + path.substring(6)
            println(path)
        }
    }
}

val manifestAttributes: MutableMap<String, *> = linkedMapOf(
        "Group" to project.group,
        "Name" to projectName,
        "Version" to project.version,
        "Authors" to authors,
        "Updated" to DateFormatUtils.format(Date(), "yyyy-MM-dd HH:mm:ssZ"),
        "Multi-Release" to true,
)

tasks.register<Jar>("sourcesJar") {
    archiveBaseName.set(projectName)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    manifest {
        attributes(manifestAttributes)
    }
}
val sourcesJar = tasks.named<Jar>("sourcesJar")

tasks.javadoc {
    options {
        this as StandardJavadocDocletOptions
        charSet(charset)
        encoding(charset)
        docEncoding(charset)
        locale("zh_CN")
        windowTitle("${projectName}-${project.version} API")
        docTitle(windowTitle)
        author(true)
        version(true)
        jFlags("-D'file.encoding'=${charset}")
    }
}

tasks.register<Jar>("javadocJar") {
    dependsOn(tasks.javadoc)
    archiveBaseName.set(projectName)
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
    manifest {
        attributes(manifestAttributes)
    }
}
val javadocJar = tasks.named<Jar>("javadocJar")

tasks.jar {
    dependsOn(sourcesJar, javadocJar)
    archiveBaseName.set(projectName)
    archiveClassifier.set("")
    manifest {
        attributes(manifestAttributes)
    }
    finalizedBy("copyToRootBuildLibs")
}
val jar = tasks.named<Jar>("jar")

tasks.create<Copy>("copyToRootBuildLibs") {
    from(sourcesJar, javadocJar, jar)
    into("${rootProject.projectDir}/build/libs")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = projectName
            version = project.version.toString()

            pom {
                name.set(projectName)
                description.set("提供一些基础的公共工具类")
                packaging = "jar"
                url.set("https://github.com/ideal-state/hyper-command")
                inceptionYear.set("2024")

                organization {
                    name.set("ideal-state")
                    url.set("https://github.com/ideal-state")
                }

                developers {
                    developer {
                        id.set("ketikai")
                        name.set("ketikai")
                        email.set("ketikai@idealstate.team")
                    }
                }

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                scm {
                    url.set("https://github.com/ideal-state/hyper-command")
                    tag.set(version)
                    connection.set("scm:git:git@github.com:ideal-state/hyper-command.git")
                    developerConnection.set("scm:git:git@github.com:ideal-state/hyper-command.git")
                }
            }

            artifact(sourcesJar)
            artifact(javadocJar)
            artifact(jar)
        }
    }
    repositories {
        maven {
            name = "local"
            url = uri("file://${projectDir}/build/repository")
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}