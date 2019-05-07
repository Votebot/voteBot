/*
 * VoteBot - A unique Discord bot for surveys
 *
 * Copyright (C) 2019  Michael Rittmeister
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow").version("4.0.3")
    kotlin("jvm") version "1.3.30"
    application
    java
}

group = "me.schlaubi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {

    // Regnum
    compile("cc.hawkbot.regnum", "client", "0.0.5")

    // Logging
    compile("org.apache.logging.log4j", "log4j-core", "2.11.2")
    compile("org.apache.logging.log4j", "log4j-slf4j-impl", "2.11.2")

    // Util
    compile("commons-cli:commons-cli:20040117.000000")
    compile("org.knowm.xchart:xchart:3.5.4")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

tasks {
    "shadowJar"(ShadowJar::class) {
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version as String)
        archiveFileName.set("${archiveBaseName.orNull}-${archiveVersion.orNull}.${archiveExtension.orNull}")
    }
    "jar"(Jar::class) {
        archiveClassifier.set("original")
    }
}

application {
    mainClassName = "me.schlaubi.votebot.BootstrapperKt"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}