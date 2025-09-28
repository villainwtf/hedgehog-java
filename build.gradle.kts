import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version ("9.1.0")
}

group = "wtf.villain"
version = runCommand("git", "rev-parse", "--short", "HEAD") ?: "0.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    compileOnly("org.projectlombok:lombok:1.18.32")

    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    minimizeJar = true
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.named<ShadowJar>("shadowJar"))

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/villainwtf/hedgehog-java")

            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

private fun runCommand(vararg command: String): String? {
    return try {
        val output = providers.exec {
            commandLine(*command)
        }

        output.standardOutput.asText.get().trim()
    } catch (_: Throwable) {
        null
    }
}