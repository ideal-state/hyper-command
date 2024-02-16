import groovy.util.Node
import org.apache.commons.lang3.time.DateFormatUtils
import java.util.*

plugins {
    id("maven-publish")
    id("signing")
}

val authors = project.ext["authors"] as String
val javaVersion = project.ext["javaVersion"] as Int
val charset = project.ext["charset"] as String

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")

    api("team.idealstate.hyper:hyper-commons-base:1.0.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testCompileOnly("org.jetbrains:annotations:24.0.0")
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
    val assetsDir = "assets/${project.name}"
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
        "Name" to project.name,
        "Version" to project.version,
        "Authors" to authors,
        "Updated" to DateFormatUtils.format(Date(), "yyyy-MM-dd HH:mm:ssZ"),
        "Multi-Release" to true,
)

tasks.register<Jar>("sourcesJar") {
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
        windowTitle("${project.name}-${project.version} API")
        docTitle(windowTitle)
        author(true)
        version(true)
        jFlags("-D'file.encoding'=${charset}")
    }
}

tasks.register<Jar>("javadocJar") {
    dependsOn(tasks.javadoc)
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
    manifest {
        attributes(manifestAttributes)
    }
}
val javadocJar = tasks.named<Jar>("javadocJar")

tasks.jar {
    dependsOn(sourcesJar, javadocJar)
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
            artifactId = project.name
            version = project.version.toString()

            pom {
                name.set(project.name)
                description.set("一个简单快速的命令系统")
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

                withXml {
                    var dependenciesNode: Node? = null
                    val compileDependencyIds = mutableSetOf<String>()
                    configurations.compileClasspath.get()
                            .resolvedConfiguration.firstLevelModuleDependencies.forEach { dependency ->
                                if (dependenciesNode == null) {
                                    dependenciesNode = asNode().appendNode("dependencies")
                                }
                                val dependencyNode = dependenciesNode!!.appendNode("dependency")
                                dependencyNode.appendNode("groupId", dependency.moduleGroup)
                                dependencyNode.appendNode("artifactId", dependency.moduleName)
                                dependencyNode.appendNode("version", dependency.moduleVersion)
                                dependencyNode.appendNode("scope", "compile")
                                compileDependencyIds.add("${dependency.moduleGroup}:${dependency.moduleName}:${dependency.moduleVersion}")
                            }
                    configurations.runtimeClasspath.get()
                            .resolvedConfiguration.firstLevelModuleDependencies.forEach { dependency ->
                                if (!compileDependencyIds.contains("${dependency.moduleGroup}:${dependency.moduleName}:${dependency.moduleVersion}")) {
                                    if (dependenciesNode == null) {
                                        dependenciesNode = asNode().appendNode("dependencies")
                                    }
                                    val dependencyNode = dependenciesNode!!.appendNode("dependency")
                                    dependencyNode.appendNode("groupId", dependency.moduleGroup)
                                    dependencyNode.appendNode("artifactId", dependency.moduleName)
                                    dependencyNode.appendNode("version", dependency.moduleVersion)
                                    dependencyNode.appendNode("scope", "runtime")
                                }
                            }
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
