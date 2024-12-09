plugins {
    id("java")
    id("signing")
    id("maven-publish")
}

group = "one.cafebabe"
version = "1.3.3"

repositories {
    mavenCentral()
}

java {
    withJavadocJar()
    withSourcesJar()
}

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    compileOnly("org.slf4j:slf4j-api:2.0.2")
    compileOnly("org.jetbrains:annotations:23.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("--module-version", version.toString()))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "businessCalendar4j"
            from(components["java"])
            pom {
                name.set("businessCalendar4j")
                description.set("businessCalendar4j")
                url.set("https://github.com/yusuke/businessCalendar4j")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("yusuke")
                        name.set("Yusuke Yamamoto")
                        email.set("yusuke@mac.com")
                    }
                }
                scm {
                    url.set("https://github.com/yusuke/businessCalendar4j")
                    connection.set("scm:git:git://github.com/yusuke/businessCalendar4j.git")
                    developerConnection.set("scm:git:git@github.com:yusuke/businessCalendar4j.git")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = project.findProperty("SONATYPE_USERNAME") as String? ?: "none"
                password = project.findProperty("SONATYPE_PASSWORD") as String? ?: "none"
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}