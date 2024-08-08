/***************************************************************************************************
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 **************************************************************************************************/

plugins {
    alias(libs.plugins.mikepenz.aboutLibraries)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ksp)
}

aboutLibraries {
    // This means that we have to generate the dependencies explicitly:
    // ./gradlew --no-configuration-cache --no-build-cache -PaboutLibraries.exportPath=src/main/res/raw/ app:exportLibraryDefinitions
    registerAndroidTasks = false
}

// Android configuration
android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.infomaniak.sync"
        resValue("string", "application_id", "com.infomaniak.sync")

        versionCode = 404010005
        versionName = "4.4.1"

        buildConfigField("long", "buildTime", "${System.currentTimeMillis()}L")

        setProperty("archivesBaseName", "kSync-$versionName")

        minSdk = 24        // Android 7.0
        targetSdk = 34     // Android 14

        buildConfigField("String", "userAgent", "\"kSync\"")

        buildConfigField("String", "CLIENT_ID", "\"CE011334-F75A-4263-9F9F-45FC5A142F59\"")

        testInstrumentationRunner = "com.infomaniak.sync.CustomTestRunner"
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    compileOptions {
        // enable because ical4android requires desugaring
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
        dataBinding = true
    }

    // Java namespace for our classes (not to be confused with Android package ID)
    namespace = "at.bitfire.davdroid"

    flavorDimensions += "distribution"
    productFlavors {
        create("ose") {
            dimension = "distribution"
//            versionNameSuffix = "-ose"
        }
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDir("$projectDir/schemas")
        }
    }

    signingConfigs {
        create("bitfire") {
            storeFile = file(System.getenv("ANDROID_KEYSTORE") ?: "/dev/null")
            storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("ANDROID_KEY_ALIAS")
            keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules-release.pro")

            isShrinkResources = true

//            signingConfig = signingConfigs.findByName("bitfire")
        }
    }

    lint {
        disable += arrayOf("GoogleAppIndexingWarning", "ImpliedQuantity", "MissingQuantity", "MissingTranslation", "ExtraTranslation", "RtlEnabled", "RtlHardcoded", "Typos", "NullSafeMutableLiveData")
    }

    packaging {
        resources {
            excludes += arrayOf("META-INF/*.md")
        }
    }

    androidResources {
        generateLocaleConfig = true
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            localDevices {
                create("virtual") {
                    device = "Pixel 3"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

configurations {
    configureEach {
        // exclude modules which are in conflict with system libraries
        exclude(module="commons-logging")
        exclude(group="org.json", module="json")

        // Groovy requires SDK 26+, and it's not required, so exclude it
        exclude(group="org.codehaus.groovy")
    }
}

dependencies {
    // core
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)
    // TODO: Reorder:
    implementation(libs.jaredrummler.colorpicker)
    implementation(libs.android.flexbox)
    implementation(libs.androidx.cardView)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.android.material)
    implementation(libs.androidx.databinding.runtime)
    coreLibraryDesugaring(libs.android.desugaring)

    // Hilt
    implementation(libs.hilt.android.base)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.hilt.android.compiler)

    // support libs
    implementation(libs.androidx.activityCompose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.base)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.security)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.work.base)

    // Jetpack Compose
    implementation(libs.compose.accompanist.permissions)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.materialIconsExtended)
    implementation(libs.compose.runtime.livedata)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.toolingPreview)

    // Glance Widgets
    implementation(libs.glance.base)
    implementation(libs.glance.material)

    // Jetpack Room
    implementation(libs.room.runtime)
    implementation(libs.room.base)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // own libraries
    implementation(libs.bitfire.cert4android)
    implementation(libs.bitfire.dav4jvm) {
        exclude(group="junit")
    }
    implementation(libs.bitfire.ical4android)
    implementation(libs.bitfire.vcard4android)

    // third-party libs
    implementation(libs.appintro)
    implementation(libs.commons.collections)
    @Suppress("RedundantSuppression")
    implementation(libs.commons.io)
    implementation(libs.commons.lang)
    implementation(libs.commons.text)
    @Suppress("RedundantSuppression")
    implementation(libs.dnsjava)
    implementation(libs.mikepenz.aboutLibraries)
    implementation(libs.nsk90.kstatemachine)
    implementation(libs.okhttp.base)
    implementation(libs.okhttp.brotli)
    implementation(libs.okhttp.logging)
    implementation(libs.openid.appauth)

    // kSync
    implementation(libs.login.infomaniak)
    implementation(libs.gson)

    // for tests
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.room.testing)

    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
}