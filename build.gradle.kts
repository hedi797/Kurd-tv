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
        
        val apkDl = java.io.File(rootDirPath, "APK_DOWNLOAD")
        if (apkDl.exists()) {
            apkDl.deleteRecursively()
        }
        apkDl.mkdirs()
        
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
                    // Copy specific name
                    val targetFile1 = java.io.File(apkDl, targetName)
                    sourceFile.copyTo(targetFile1, true)
                    
                    // Copy default name
                    val targetFile2 = java.io.File(apkDl, defaultTargetName)
                    sourceFile.copyTo(targetFile2, true)
                    
                    // Copy to .build-outputs
                    sourceFile.copyTo(java.io.File(extDl, targetName), true)
                    sourceFile.copyTo(java.io.File(extDl, defaultTargetName), true)
                    
                    println("SUCCESS: Copied $targetName and $defaultTargetName (Size: ${size / 1024} KB) to /APK_DOWNLOAD and .build-outputs")
                } else {
                    println("WARNING: Source file exists but is 0 bytes: ${sourceFile.absolutePath}")
                }
            } else {
                println("INFO: Source file not found: ${sourceFile.absolutePath} (Not compiled or skipped)")
            }
        }
    }
}

tasks.register("checkFiles") {
    doLast {
        val rootDirPath = rootProject.rootDir.absolutePath
        val apkDl = java.io.File(rootDirPath, "APK_DOWNLOAD")
        println("=== APK_DOWNLOAD DIRECTORY CONTENTS AND SIZES ===")
        if (apkDl.exists()) {
            val files = apkDl.listFiles()
            if (files != null) {
                for (file in files) {
                    println("${file.name}: ${file.length()} bytes")
                }
            } else {
                println("No files found in APK_DOWNLOAD")
            }
        } else {
            println("APK_DOWNLOAD folder does not exist")
        }
    }
}

