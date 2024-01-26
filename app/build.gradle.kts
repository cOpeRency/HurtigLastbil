val androidCoreVersion="1.12.0"
val androidAppCompatVersion="1.6.1"
val androidMaterialVersion="1.11.0"
val androidConstraintLayoutVersion="2.1.4"
val junitVersion="4.13.2"
val assertJVersion="3.24.2"
val mockkVersion="1.13.8"
val robolectricVersion="4.11.1"
val espressoVersion="3.5.1"
val kotlinSerializationVersion="1.6.1"
val junit5Version="1.1.5"
val espressoCoreVersion="3.5.1"


plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.sonarqube") version "4.4.1.3373"
    kotlin("plugin.serialization") version "1.9.10"
}

android {
    namespace = "fr.hurtiglastbil"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.hurtiglastbil"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:$androidCoreVersion")
    implementation("androidx.appcompat:appcompat:$androidAppCompatVersion")
    implementation("com.google.android.material:material:$androidMaterialVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    implementation("androidx.constraintlayout:constraintlayout:$androidConstraintLayoutVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    androidTestImplementation("androidx.test.ext:junit:$junit5Version")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoCoreVersion")
    testImplementation("org.robolectric:robolectric:$robolectricVersion")
}

sonar {
    properties {
        property("sonar.projectName", "HurtigLastbil")
        property("sonar.projectKey", "HurtigLastbil")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.token", "sqp_dc869d0b3da5b1ef333c636b5550a11d7a71f941")
        property("sonar.gradle.skipCompile", "true")
    }
}
