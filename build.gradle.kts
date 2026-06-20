// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
}

tasks.register("copyApk") {
    dependsOn(
        ":app:assembleDebug",
        ":app:bundleDebug",
        ":app:assembleRelease",
        ":app:bundleRelease"
    )
    val rootDirPath = rootProject.rootDir.absolutePath
    doLast {
        val extDl = java.io.File(rootDirPath, ".build-outputs")
        extDl.mkdirs()
        
        val targets = listOf(
            Triple("app/build/outputs/apk/debug/app-debug.apk", "kurd-tv-debug.apk", "app-debug.apk"),
            Triple("app/build/outputs/bundle/debug/app-debug.aab", "kurd-tv-debug.aab", "app-debug.aab"),
            Triple("app/build/outputs/apk/release/app-release.apk", "kurd-tv-release.apk", "app-release.apk"),
            Triple("app/build/outputs/bundle/release/app-release.aab", "kurd-tv-release.aab", "app-release.aab")
        )
        
        println("=== COPYING AND VALIDATING COMPILED PACKAGES ===")
        for (t in targets) {
            val sourcePath = t.first
            val targetName = t.second
            val defaultTargetName = t.third
            val sourceFile = java.io.File(rootDirPath, sourcePath)
            if (sourceFile.exists()) {
                val size = sourceFile.length()
                if (size > 0) {
                    // Copy to .build-outputs
                    sourceFile.copyTo(java.io.File(extDl, targetName), true)
                    sourceFile.copyTo(java.io.File(extDl, defaultTargetName), true)
                    
                    println("SUCCESS: Copied $targetName and $defaultTargetName (Size: ${size / 1024} KB) to .build-outputs")
                } else {
                    println("WARNING: Source file exists but is 0 bytes: ${sourceFile.absolutePath}")
                }
            } else {
                println("INFO: Source file not found: ${sourceFile.absolutePath} (Not compiled or skipped)")
            }
        }
    }
}





