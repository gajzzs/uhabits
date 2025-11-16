plugins {
    kotlin("jvm")
    alias(libs.plugins.compose)
}

dependencies {
    implementation(project(":uhabits-core"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "org.isoron.uhabits.desktop.MainKt"
    }
}