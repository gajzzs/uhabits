plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Loop Habit Tracker iOS"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        framework {
            baseName = "uhabits"
        }
    }

    sourceSets {
        val iosMain by creating {
            dependencies {
                implementation(project(":uhabits-core"))
            }
        }
    }
}