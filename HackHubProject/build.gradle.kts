plugins {
    id("java")
    id("application")
}

group = "it.hackhub"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

application {
    mainClass.set("it.hackhub.HackHubApplication")
}

tasks.test {
    useJUnitPlatform()
}
