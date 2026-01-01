plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    kotlin("plugin.jpa") version "2.3.0"

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
}

group = "com.cankolay.trash.core"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    implementation(libs.bundles.spring.boot)

    implementation(libs.spring.doc)
    implementation(libs.scalar)

    implementation(libs.jackson)

    implementation(libs.bundles.flyway)
    runtimeOnly(libs.postgresql)

    implementation(libs.bouncycastle)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt)

    implementation(libs.yauaa)

    developmentOnly(libs.spring.boot.devtools)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}