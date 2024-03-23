import net.thebugmc.gradle.sonatypepublisher.PublishingType.USER_MANAGED

plugins {
    id("java")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
}

group = "io.github.bitterfox"
version = "0.0.4-SNAPSHOT"

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.toVersion("1.8")
    targetCompatibility = JavaVersion.toVersion("1.8")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.hamcrest:hamcrest:2.2")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

apply(plugin = "net.thebugmc.gradle.sonatype-central-portal-publisher")

signing {
    useGpgCmd()
}

centralPortal {
    publishingType = USER_MANAGED

    name = project.name
    description = "Hamlet: A Fluent Object Matcher For Hamcrest"

    pom {
        url = "https://github.com/bitterfox/json-string-template"
        licenses {
            license {
                name = "Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }
        developers {
            developer {
                name = "bitter_fox"
            }
        }
        scm {
            url = "https://github.com/bitterfox/hamlet"
            connection = "scm:git:git://github.com/bitterfox/hamlet.git"
            developerConnection = "scm:git:git@github.com:bitterfox/hamlet.git"
        }
    }

    jarTask = tasks.jar

    sourcesJarTask = tasks.create<Jar>("sourcesEmptyJar") {
        archiveClassifier = "sources"
    }
    javadocJarTask = tasks.create<Jar>("javadocEmptyJar") {
        archiveClassifier = "javadoc"
    }
}
