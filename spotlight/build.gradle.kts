plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mavenpublish)
    id("maven-publish")
}

android {
    namespace = "com.banglalogic.spotlight"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

mavenPublishing {
    coordinates(
        "io.github.rokon11ahmed",
        "spotlight",
        "1.0.2")

    pom {
        name.set("Spotlight library")
        description.set("A lightweight, fully customizable Spotlight library for Android.")
        inceptionYear.set("2025")
        url.set("https://github.com/Rokon11Ahmed/spotlight")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("Rokon11Ahmed")
                name.set("Md Rokonuzzaman")
                url.set("https://github.com/Rokon11Ahmed")
            }
        }
        scm {
            url.set("https://github.com/Rokon11Ahmed/spotlight")
            connection.set("scm:git:git://github.com/Rokon11Ahmed/spotlight.git")
            developerConnection.set("scm:git:ssh://git@github.com/Rokon11Ahmed/spotlight.git")
        }
    }

    publishToMavenCentral(automaticRelease = true)

    signAllPublications()
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "io.github.rokon11ahmed"
                artifactId = "spotlight"
                version = "1.0.2"
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.withType<Sign>())
}