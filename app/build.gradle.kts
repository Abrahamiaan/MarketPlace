import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.marketplace"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.marketplace"
        minSdk = 28
        targetSdk = 34
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
        }
    }

    packagingOptions {
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/LICENSE.md")
    }

    viewBinding {
        enable = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.android.libraries.places:places:3.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // UI Helper Dependencies
    implementation ("com.etebarian:meow-bottom-navigation:1.2.0")
    implementation ("com.github.salehyarahmadi:AndExAlertDialog:v1.0.1")
    implementation ("com.civitasv.iosLike:dialog:0.1.3")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.intuit.sdp:sdp-android:1.0.3")
    implementation ("com.github.lucifer0987:horizontal-rating-library:0.1.0")
    implementation ("com.github.imtuann:FloatingActionButtonExpandable:1.1.2")
    implementation ("de.blox.treeview:treeview:0.1.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.0")

    implementation ("com.sun.mail:android-mail:1.6.6")
    implementation ("com.sun.mail:android-activation:1.6.6")

    // Firebase Dependencies
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.android.gms:play-services-auth:19.2.0")
    implementation ("com.google.firebase:firebase-analytics:20.0.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.firebase:firebase-firestore:24.6.0")
}