plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.maps.compose)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.kotlinx.coroutines.play.services)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}