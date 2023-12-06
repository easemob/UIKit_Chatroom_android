import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.agora.chatroom"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.agora.chatroom"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField("String", "CHATROOM_APP_KEY", "\"${localProperties.getProperty("CHATROOM_APP_KEY")}\"")
        buildConfigField("String", "REQUEST_HOST", "\"${localProperties.getProperty("REQUEST_HOST")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    // UI
    implementation(project(mapOf("path" to ":ChatroomUIKit")))

    // Http Libraries
    implementation("com.squareup.okhttp3:okhttp:3.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
}