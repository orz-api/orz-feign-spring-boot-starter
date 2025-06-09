plugins {
    signing
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "io.github.orz-api"
version = "0.0.3"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withJavadocJar()
    withSourcesJar()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    configureEach {
        resolutionStrategy {
            cacheDynamicVersionsFor(0, "seconds")
            cacheChangingModulesFor(0, "seconds")
        }
    }
}

repositories {
    maven {
        name = "Central Portal Snapshots"
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        content {
            includeVersionByRegex(".*", ".*", ".*SNAPSHOT")
        }
    }
    mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
    api("io.github.orz-api:orz-web-spring-boot-starter:0.0.3")
    api("org.springframework.cloud:spring-cloud-starter-openfeign")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.javadoc {
    val options = (options as StandardJavadocDocletOptions)
    options.encoding("UTF-8")
    options.addStringOption("Xdoclint:none", "-quiet")
    if (JavaVersion.current().isJava9Compatible) {
        options.addBooleanOption("html5", true)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name = "orz-feign-spring-boot-starter"
                description = "orz-api feign spring boot starter"
                url = "https://github.com/orz-api/orz-feign-spring-boot-starter"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit/"
                    }
                }
                developers {
                    developer {
                        id = "reset7523"
                        name = "reset7523"
                        email = "reset7523@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com:orz-api/orz-feign-spring-boot-starter.git"
                    developerConnection = "scm:git:git://github.com:orz-api/orz-feign-spring-boot-starter.git"
                    url = "https://github.com/orz-api/orz-feign-spring-boot-starter"
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(
                if (version.toString().endsWith("SNAPSHOT")) "https://central.sonatype.com/repository/maven-snapshots/"
                else "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
            )
            credentials {
                val orzSonatypeUsername: String by project
                val orzSonatypePassword: String by project
                username = orzSonatypeUsername
                password = orzSonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
