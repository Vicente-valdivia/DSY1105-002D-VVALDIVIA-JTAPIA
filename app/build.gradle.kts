plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.dsy1105_002d_vvaldivia_jtapia"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.dsy1105_002d_vvaldivia_jtapia"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation("io.kotest:kotest-runner-junit5:5.8.6")
    testImplementation("io.kotest:kotest-assertions-core:5.8.8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.6")
    testImplementation("io.mockk:mockk:1.13.10")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.2")

    // Kotest para pruebas de lógica y asincronía
    testImplementation("io.kotest:kotest-runner-junit5:5.8.6")
    testImplementation("io.kotest:kotest-assertions-core:5.8.8")

// JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.6")

// MockK para simular objetos y dependencias
    testImplementation("io.mockk:mockk:1.13.10")

// Coroutines Test para manejar llamadas asincrónicas
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

// Compose UI Test (Pruebas instrumentadas)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.2")

}
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}