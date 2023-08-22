package com.skin.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.cxx.logging.warnln
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class PackageSkinTask : DefaultTask() {
    @InputDirectory
    lateinit var skinDir: File

    @OutputDirectory
    lateinit var outputDir: File

    @TaskAction
    fun packageSkin() {
        val resourcesDir = File(skinDir, "res")
        val manifestFile = File(skinDir, "AndroidManifest.xml")
        if (!resourcesDir.exists() || !resourcesDir.isDirectory) {
            warnln("${TAG}The res folder could not be found in ${skinDir.absolutePath}!")
            return
        }
        if (!manifestFile.exists() || !manifestFile.isFile) {
            warnln("${TAG}The AndroidManifest file could not be found in ${skinDir.absolutePath}!")
            return
        }

        outputDir.deleteOnExit()
        val rOutputDir = outputDir
        val apOutputFile = File(outputDir, "${skinDir.name}.skin")
        rOutputDir.mkdirs()
        apOutputFile.createNewFile()

        val appExtension = project.extensions.findByType(AppExtension::class.java)
        val sdkDir = appExtension?.sdkDirectory
        // aapt路径
        val aapt = File("$sdkDir/build-tools/${appExtension?.buildToolsVersion}/aapt")
        // android.jar路径
        val androidJar = File("$sdkDir/platforms/${appExtension?.compileSdkVersion}/android.jar")

        project.exec {
            it.commandLine(
                aapt.absolutePath,
                "package",
                "-f",
                "-m",
                "-J",
                rOutputDir.absolutePath,
                "-F",
                apOutputFile.absolutePath,
                "-S",
                resourcesDir.absolutePath,
                "-M",
                manifestFile.absolutePath,
                "-I",
                androidJar.absolutePath
            )
        }

        println("${TAG}outputPath: ${outputDir.absolutePath}")
    }
}