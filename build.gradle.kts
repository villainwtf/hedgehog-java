import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("com.gradleup.shadow") version ("9.1.0")
}

group = "wtf.villain"
version = "1.0.0"

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
    minimizeJar = true
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString()
    )

    pom {
        name.set(project.name)
        description.set("A Java library for interacting with the Posthog API.")

        inceptionYear.set("2024")
        url.set("https://github.com/villainwtf/hedgehog-java/")

        licenses {
            license {
                name.set("MIT License")
                url.set("http://www.opensource.org/licenses/mit-license.php")
                distribution.set("http://www.opensource.org/licenses/mit-license.php")
            }
        }

        developers {
            developer {
                id.set("villain")
                name.set("Villain developers")
                url.set("https://github.com/villainwtf")
            }
        }

        scm {
            url.set("https://github.com/villainwtf/hedgehog-java")
            connection.set("scm:git:git://github.com/villainwtf/hedgehog-java.git")
            developerConnection.set("scm:git:ssh://git@github.com/villainwtf/hedgehog-java.git")
        }
    }
}
