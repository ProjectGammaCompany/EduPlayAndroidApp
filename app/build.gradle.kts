
import com.android.build.api.variant.BuildConfigField

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.eduplay.moblie"
    compileSdkVersion(35)

    defaultConfig {
        applicationId = "com.eduplay.moblie"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            //TODO("заменить на реальный адрес бекэнда")
            buildConfigField("String", "BACKEND_URL", "\"http://localhost:80\"")


        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            buildConfigField("String", "BACKEND_URL", "\"http://192.168.1.227:8080/ \"")
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.ui.test.junit4)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //retrofit
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)


    //async images
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // datastore
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.rxjava2)
    implementation(libs.androidx.datastore.preferences.rxjava3)

    implementation(libs.kotlin.reflect)

    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime.ktx)

    //mlKit for qr code scanning
    implementation(libs.barcode.scanning)

    //cameraX for active scanning
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // permissions
    implementation(libs.accompanist.permissions)
}