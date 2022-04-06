plugins {
    `java-library`
    id("io.quarkus.extension")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform(libs.quarkus.bom))

    implementation(libs.quarkus.core.runtime)
    implementation(libs.quarkus.vertx.http.runtime)
    implementation(libs.quarkus.arc.runtime)
}
