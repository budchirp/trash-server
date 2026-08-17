plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.spring") version libs.versions.kotlin
    kotlin("plugin.jpa") version libs.versions.kotlin

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
}

group = "dev.cankolay.trash.server"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    implementation(libs.bundles.spring.boot)

    implementation(libs.springdoc)

    implementation(libs.jackson)

    runtimeOnly(libs.postgresql)

    implementation(libs.bouncycastle)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt)

    implementation(libs.yauaa)

    implementation(libs.aws.s3)

    developmentOnly(libs.spring.boot.devtools)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
