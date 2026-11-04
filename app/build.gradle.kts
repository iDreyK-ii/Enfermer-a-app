import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val cloudUrl = providers.gradleProperty("IDREYK_SUPABASE_URL")
    .orElse(providers.environmentVariable("IDREYK_SUPABASE_URL")).getOrElse("").trim().trimEnd('/')
val cloudKey = providers.gradleProperty("IDREYK_SUPABASE_PUBLISHABLE_KEY")
    .orElse(providers.environmentVariable("IDREYK_SUPABASE_PUBLISHABLE_KEY")).getOrElse("").trim()
require((cloudUrl.isEmpty() && cloudKey.isEmpty()) ||
    (Regex("https://[a-z0-9-]+\\.supabase\\.co").matches(cloudUrl) &&
        Regex("sb_publishable_[A-Za-z0-9_-]{8,500}").matches(cloudKey))) {
    "Configura URL HTTPS de Supabase y clave publicable juntas. Nunca uses claves secretas/service_role en Android."
}

android {
    namespace = "com.idreyk.enfermeria360"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.idreyk.enfermeria360"
        minSdk = 26
        targetSdk = 35
        versionCode = 11
        versionName = "0.9.1-dev"
        buildConfigField("String", "CLOUD_URL", "\"$cloudUrl\"")
        buildConfigField("String", "CLOUD_PUBLISHABLE_KEY", "\"$cloudKey\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions { unitTests.isIncludeAndroidResources = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    testImplementation("org.json:json:20240303")
}

tasks.withType<Test>().configureEach {
    maxHeapSize = "384m"
    maxParallelForks = 1
    // Isolate Android API/native-graphics sandboxes; no accumulating JNI caches across suites.
    forkEvery = 1
    jvmArgs("-XX:+UseSerialGC", "-XX:MaxMetaspaceSize=192m")
    systemProperty("robolectric.dependency.repo.url", "https://repo.maven.apache.org/maven2")
    // Las bibliotecas de pruebas son caché regenerable, no archivos del aplicativo.
    systemProperty("maven.repo.local", rootProject.file(".cache/robolectric-maven").absolutePath)
    systemProperty("idreyk.captureDir", rootProject.file(".cache/native-atlas-v22").absolutePath)
    // Native libraries are large. A tmpfs /tmp consumes scarce RAM; use regenerable disk cache.
    val testTmp = rootProject.file(".cache/test-tmp")
    doFirst { testTmp.mkdirs() }
    systemProperty("java.io.tmpdir", testTmp.absolutePath)
    testLogging {
        events("failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
