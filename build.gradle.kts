import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import net.ornithemc.ploceus.api.PloceusGradleExtensionApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("dev.kikugie.loom-back-compat")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("ploceus") version "1.19.1" apply false
}

val isOrnithe = sc.current.version == "1.8.9"
val ploceus = if (isOrnithe) {
    pluginManager.apply("net.fabricmc.fabric-loom-remap")
    pluginManager.apply("ploceus")

    configurations.configureEach {
        exclude(group = "org.lwjgl.lwjgl")
    }

    extensions.getByType<PloceusGradleExtensionApi>().apply {
        setIntermediaryGeneration(2)
    }
} else {
    null
}

version = "${property("mod.version")}+${sc.current.project}"
base.archivesName = property("mod.id") as String

val requiredJava = if (isOrnithe || sc.current.parsed >= "26.1") JavaVersion.VERSION_25 else JavaVersion.VERSION_21

val standalone = "org/polyfrost/keyframe/standalone"
val modernStandalone = listOf("compat/Compat", "export/AudioCapture", "playback/Keyframe", "export/PreviewFrame", "export/RunOnceTest").map { "$standalone/$it.kt" } + "$standalone/mixin/Mixin_*.java"
val sharedAddon = listOf("ui/UiLayout", "shadow/ShadowRecorder", "shadow/ShadowReplayZip", "shadow/ShadowReplayConfig", "shadow/ShadowReplayCore", "camera/CameraFrames", "ui/ScreenWire").flatMap { listOf("$it.java", "${it}Test.java") }
val ornitheKeeps = listOf("standalone/", "boot/", "ornithe/", "mixin/ornithe/")
sourceSets.configureEach {
    val excluded = buildList {
        if (isOrnithe) addAll(modernStandalone) else add("**/ornithe/**")
    }
    if (isOrnithe) {
        val keeps = ornitheKeeps
        val shared = sharedAddon
        java.exclude {
            val path = it.relativePath.pathString.removePrefix("org/polyfrost/keyframe/")
            !it.isDirectory && path != it.relativePath.pathString && keeps.none(path::startsWith) && path !in shared
        }
    }
    java.exclude(excluded)
    kotlin.exclude(excluded)
}

repositories {
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" } }
        filter { includeGroup("com.terraformersmc") }
    }
    maven("https://repo.polyfrost.org/releases") { name = "Polyfrost" }
    maven("https://repo.polyfrost.org/snapshots") { name = "PolyfrostSnapshots" } // NEW LINE
    maven("https://maven.cloverclient.com/releases") {
        content { includeGroup("pl.tomgirl") }
    }
    mavenLocal {
        content {
            includeModuleByRegex("org\\.polyfrost", "flashback_legacy.*")
            includeGroup("org.polyfrost.oneconfig")
        }
    }
    maven("https://redirector.kotlinlang.org/maven/compose-dev")
    google()
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    if (isOrnithe) {
        mappings(ploceus!!.featherMappings(property("deps.feather") as String))
        ploceus.dependOsl(property("deps.osl") as String)
        val legacy = "org.polyfrost:flashback_legacy:${property("deps.flashback_legacy")}"
        if (findProperty("standalone") == null) modImplementation(legacy) else modCompileOnly(legacy)
        compileOnly("org.polyfrost:flashback_legacy-patched-flashback:${property("deps.flashback_legacy")}")
