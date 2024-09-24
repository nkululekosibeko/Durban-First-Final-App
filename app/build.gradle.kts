plugins {
    id("com.android.application")  // Correct plugin for Android apps in Kotlin DSL
    id("com.google.gms.google-services")  // Correct plugin for Google services
}

android {
    namespace = "com.example.durbanfirst"
    compileSdk = 34  // Correct in Kotlin DSL (no "Version" suffix)

    defaultConfig {
        applicationId = "com.example.durbanfirst"
        minSdk = 27  // Correct in Kotlin DSL
        targetSdk = 34  // Correct in Kotlin DSL
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.2")  // Correct string format in Kotlin DSL
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.activity:activity:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase dependencies using the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))  // Ensure double quotes
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

// Apply Google services plugin in Kotlin DSL
apply(plugin = "com.google.gms.google-services")
