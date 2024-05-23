plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    signingConfigs {
        create("release") {
            storeFile =
                file("C:\\Users\\Admin\\AndroidStudioProjects\\WeatherForecast\\weather_forecast.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
        }
    }
    namespace = "com.natureweather.sound.temperature"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.natureweather.sound.temperature"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("release")
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Gif
    implementation (libs.android.gif.drawable)

    //Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    //use in development
    implementation (libs.logging.interceptor)

    implementation (libs.adapter.rxjava2)
    implementation (libs.glide)

    //Jsoup
    implementation (libs.jsoup)


    implementation ("com.github.Triggertrap:SeekArc:v1.1")

    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")

    implementation (libs.androidx.annotation)

}