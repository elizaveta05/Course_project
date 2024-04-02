plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.course_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.course_project"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    // Библиотека Firebase Realtime Database для работы с базой данных в реальном времени
    implementation ("com.google.firebase:firebase-database:20.3.1")

    // Библиотека Firebase Authentication для аутентификации пользователей
    implementation ("com.google.firebase:firebase-auth:22.3.1")

    // Библиотека Firebase Firestore для работы с облачной базой данных Firestore
    implementation ("com.google.firebase:firebase-firestore:24.10.3")

    // Библиотека Picasso для загрузки и отображения изображений
    implementation ("com.squareup.picasso:picasso:2.71828")

    // Библиотека для работы с AppCompat компонентами
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Библиотека Material Design для создания стильного пользовательского интерфейса
    implementation("com.google.android.material:material:1.11.0")

    // Библиотека ConstraintLayout для создания сложных макетов пользовательского интерфейса
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Библиотека Legacy Support для обеспечения обратной совместимости
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Библиотека Firebase Analytics для аналитики и отслеживания событий
    implementation("com.google.firebase:firebase-analytics:21.6.1")

    // Библиотека Activity из пакета androidx для работы со структурой Activity
    implementation("androidx.activity:activity:1.8.2")

    // Зависимость для тестирования с использованием JUnit
    testImplementation("junit:junit:4.13.2")

    // Зависимость для Android тестов с использованием JUnit
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    // Зависимость для Android тестов с использованием Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}