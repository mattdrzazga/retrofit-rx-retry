object Versions {
    const val kotlin = "1.4.32"
    const val retrofit = "2.9.0"
    const val rxJava = "3.0.11"
}

object Dependencies {
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val rxJava3 = "io.reactivex.rxjava3:rxjava:${Versions.rxJava}"

    const val ktx = "androidx.core:core-ktx:1.3.2"
    const val appCompat = "androidx.appcompat:appcompat:1.2.0"
    const val material = "com.google.android.material:material:1.3.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val junit = "junit:junit:4.13.2"
    const val junitExt = "androidx.test.ext:junit:1.1.2"
    const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
}

object Android {
    const val compileSdk = 30
    const val minSdk = 21
    const val targetSdk = 30
    const val buildTools = "30.0.3"
}