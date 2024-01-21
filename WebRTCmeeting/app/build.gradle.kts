plugins {
    id("com.android.application")
}

android {
    namespace = "net.qingbolan.webrtcmeeting"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.qingbolan.webrtcmeeting"
        minSdk = 26
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // gson库各版本见 https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.9.0")
    // https://mvnrepository.com/artifact/org.webrtc/google-webrtc
    implementation("org.webrtc:google-webrtc:1.0.28513")
    // socketio库各版本见 https://mvnrepository.com/artifact/io.socket/socket.io-client
    implementation("io.socket:socket.io-client:1.0.1")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}