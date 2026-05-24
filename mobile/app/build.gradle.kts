import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val keystoreProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(localPropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["PRESENCE_STORE_FILE"] as String)
            storePassword = keystoreProperties["PRESENCE_STORE_PASSWORD"] as String
            keyAlias = keystoreProperties["PRESENCE_KEY_ALIAS"] as String
            keyPassword = keystoreProperties["PRESENCE_KEY_PASSWORD"] as String
        }
    }

    namespace = "com.presenceprotocol.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.presenceprotocol.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "role"
    productFlavors {
        create("client") {
            dimension = "role"
            buildConfigField("String", "BLE_ROLE", "\"CLIENT_ONLY\"")
            applicationIdSuffix = ".client"
            versionNameSuffix = "-client"
        }
        create("server") {
            dimension = "role"
            buildConfigField("String", "BLE_ROLE", "\"SERVER_ONLY\"")
            applicationIdSuffix = ".server"
            versionNameSuffix = "-server"
        }
        create("both") {
            dimension = "role"
            buildConfigField("String", "BLE_ROLE", "\"BOTH\"")
            applicationIdSuffix = ".both"
            versionNameSuffix = "-both"
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources.excludes += "META-INF/*"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation(project(":feature-relay"))
    implementation(project(":core-common"))
    implementation(project(":core-crypto"))
    implementation(project(":core-storage"))
    implementation(project(":domain"))
    implementation(project(":data-ble"))
    implementation(project(":data-storage"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.coroutines.android)

    implementation(libs.mlkit.barcode)
    implementation("com.google.android.material:material:1.11.0")
}
