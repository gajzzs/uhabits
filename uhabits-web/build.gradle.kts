plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":uhabits-core"))
            }
        }
    }
}