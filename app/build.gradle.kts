plugins {
    id("com.android.application")
    id("com.google.gms.google-services")  // Applied within the plugins block
}

android {
    namespace = "com.example.durbanfirst"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.durbanfirst"
        minSdk = 27
        targetSdk = 34
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
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.activity:activity:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase dependencies using Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    // JavaMail dependency
    implementation("com.sun.mail:javax.mail:1.6.2") // or a newer version if available

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

// Ensure proper task ordering to avoid conflicts
afterEvaluate {
    tasks.named("mergeDebugResources").configure {
        mustRunAfter(tasks.named("processDebugGoogleServices"))
    }
}
