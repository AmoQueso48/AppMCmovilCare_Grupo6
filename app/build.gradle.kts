import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use {
        keystoreProperties.load(it)
    }
}

android {
    namespace = "com.example.mcmovilcare"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.mcmovilcare"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }

            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            /*excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"*/
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

    // MockK (Pruebas unitarias locales)
    testImplementation("io.mockk:mockk:1.13.10")

    // Corrutinas Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

    // MockK (Pruebas instrumentadas Android)
    androidTestImplementation("io.mockk:mockk-android:1.14.12")
}