plugins {
    java
    application
    id("io.freefair.lombok") version "8.6"  // Opzionale
}

group = "it.hackhub"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories { mavenCentral() }

dependencies {
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    //runtimeOnly("com.h2database:h2")

    // SLF4J (opzionale, toglilo se errore logger)
     implementation("org.slf4j:slf4j-api:2.0.9")
     implementation("org.slf4j:slf4j-simple:2.0.9")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("it.hackhub.HackHubApplication")
}

tasks.test { useJUnitPlatform() }
