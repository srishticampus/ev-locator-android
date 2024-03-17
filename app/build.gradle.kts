plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.project.evlocator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.evlocator"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
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
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //lottie animation
    implementation("com.airbnb.android:lottie:3.4.0")
    //bottom navigation
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation ("androidx.core:core-splashscreen:1.1.0-alpha02")

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    // Stripe Android SDK
    implementation("com.stripe:stripe-android:20.34.4")
    implementation("com.stripe:stripe-java:24.0.0")
    //play services
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.firebase:firebase-messaging:23.4.1")

    //notification
    implementation("com.github.isradeleon:Notify-Android:1.0.5")
    implementation("androidx.activity:activity:1.8.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}