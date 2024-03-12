plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.adentech.artai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adentech.artai"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1"
        resourceConfigurations += listOf("en", "de", "es", "fr", "it", "pt", "tr", "ru", "ar")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        release {
            isShrinkResources = false
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    kotlin {
        sourceSets.all {
            languageSettings {
                languageVersion = "2.0"
            }
        }
    }
}

dependencies {

    implementation("androidx.camera:camera-view:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.window:window:1.2.0")
    val lifecycleVersion = "2.7.0"
    val navVersion = "2.7.7"


    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    // Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Billing
    implementation("com.android.billingclient:billing-ktx:6.1.0")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("com.google.guava:guava:24.1-jre")
    // Also declare the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    //Desugar
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-config")
    // Google Ads
    implementation("com.google.android.gms:play-services-ads:23.0.0")

    //HILT
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    //Gson
    implementation("com.google.code.gson:gson:2.10")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    //Lifecycle
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // Lifecycles only (without ViewModel or LiveData)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    // Saved state module for ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.7.0-alpha09")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    //Multidex
    implementation("com.android.support:multidex:1.0.3")

    // In app review
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")
    kapt("com.github.bumptech.glide:compiler:4.14.2")
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    //SlideView
    implementation("com.ncorti:slidetoact:0.3.0")
    implementation("commons-io:commons-io:2.6")

    // Photoview
    implementation("com.github.chrisbanes:PhotoView:2.2.0")

    //RoundedImageView
    implementation("com.makeramen:roundedimageview:2.3.0")

    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")

    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Lottie
    implementation ("com.airbnb.android:lottie:5.2.0")

}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}


