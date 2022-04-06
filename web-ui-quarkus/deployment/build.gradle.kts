plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform(libs.quarkus.bom))

    implementation(projects.webUi)
    implementation(projects.webUiQuarkus.runtime)

    implementation(libs.quarkus.core.deployment)
    implementation(libs.quarkus.vertx.http.deployment)
    implementation(libs.quarkus.arc.deployment)
}
