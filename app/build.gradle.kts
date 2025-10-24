plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle.plugin)
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

android {
    namespace = "com.example.citiway"
    compileSdk = 36
    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.example.citiway"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("Double", "CAPE_TOWN_LAT", "-33.9249")
        buildConfigField("Double", "CAPE_TOWN_LNG", "18.4241")
        buildConfigField("Double", "SOUTHWEST_CAPE_TOWN_LAT", "-34.3")
        buildConfigField("Double", "SOUTHWEST_CAPE_TOWN_LNG", "18.0")
        buildConfigField("Double", "NORTHEAST_CAPE_TOWN_LAT", "-33.5")
        buildConfigField("Double", "NORTHEAST_CAPE_TOWN_LNG", "18.9")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
}

dependencies {
    // For Room testing
    testImplementation("androidx.room:room-testing:2.6.1")
// For JUnit tests
    testImplementation("junit:junit:4.13.2")
// For coroutines test support
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
// For AndroidX test utilities
    testImplementation("androidx.test:core:1.5.0")

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.maps.compose)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.datastore.preferences)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.play.services.basement)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}