plugins {
    `java-library`
    id("io.quarkus")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform(libs.quarkus.bom))

    implementation(projects.webUiQuarkus.runtime)
    implementation(libs.quarkus.resteasy.core)
}

/* Fix for Quarkus/Gradle issues */
/*tasks.quarkusGenerateCode {
    mustRunAfter(projects.webUiQuarkus.deployment)
    mustRunAfter(projects.webUi)

    doFirst {
        mkdir("${projects.webUi.dependencyProject.buildDir}/classes/java/main")
    }
}

tasks.quarkusGenerateCodeTests {
    mustRunAfter(projects.webUiQuarkus.deployment)
    mustRunAfter(projects.webUi)
}*/
