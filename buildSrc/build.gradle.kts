import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ca.cutterslade.gradle.analyze.AnalyzeDependenciesTask

plugins {
    `kotlin-dsl`
    id("ca.cutterslade.analyze") version "1.9.0" apply true
}

group = "no.nav.arbeidsgiver"
version = "unspecified"

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<Wrapper> {
        gradleVersion = "7.5.1"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

    withType<AnalyzeDependenciesTask> {
        warnUsedUndeclared = true
        warnUnusedDeclared = true
    }
}