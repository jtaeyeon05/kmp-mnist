import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

val releaseName = "1.0.4"
val releaseCode = 5
val buildNumber = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))!!
val buildInfoPackage = "io.github.jtaeyeon05.kmp_mnist.buildinfo"
val buildInfoDir = layout.buildDirectory.dir("generated/sources/buildInfo/kotlin")

@CacheableTask
abstract class GenerateBuildInfoTask : DefaultTask() {
    @get:Input
    abstract val releaseNameProp: Property<String>
    @get:Input
    abstract val releaseCodeProp: Property<Int>
    @get:Input
    abstract val buildNumberProp: Property<String>
    @get:Input
    abstract val packageProp: Property<String>
    @get:OutputDirectory
    abstract val outDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val out = outDir.get().asFile
        val packagePath = packageProp.get().replace('.', '/')
        val file = File(out, "$packagePath/BuildInfo.kt")
        file.parentFile.mkdirs()
        file.writeText(
            """
            package ${packageProp.get()}
            
            object BuildInfo {
                const val RELEASE_NAME = "${releaseNameProp.get()}"
                const val RELEASE_CODE = "${releaseCodeProp.get()}"
                const val BUILD_NUMBER  = "${buildNumberProp.get()}"
            }
            
            """.trimIndent()
        )
        print(">> Generated BuildInfo.kt at: ${file.absolutePath}")
    }
}

val generateBuildInfo by tasks.register<GenerateBuildInfoTask>("generateBuildInfo") {
    releaseNameProp.set(releaseName)
    releaseCodeProp.set(releaseCode)
    buildNumberProp.set(buildNumber)
    packageProp.set(buildInfoPackage)
    outDir.set(buildInfoDir)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(buildInfoDir)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    val syncTaskNames = setOf("prepareKotlinBuildScriptModel", "prepareKotlinIdeaImport")
    tasks.matching { it.name in syncTaskNames }.all {
        dependsOn(generateBuildInfo)
    }
    targets.all {
        compilations.all {
            compileTaskProvider.configure { dependsOn(generateBuildInfo) }
        }
    }
}

android {
    namespace = "io.github.jtaeyeon05.kmp_mnist"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.jtaeyeon05.kmp_mnist"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = releaseCode
        versionName = releaseName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "io.github.jtaeyeon05.kmp_mnist.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.jtaeyeon05.kmp_mnist"
            packageVersion = releaseName
        }
    }
}
