package com.skin.plugin

import com.android.build.gradle.internal.cxx.logging.errorln
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

const val TAG = "SkinPlugin: "

class SkinPlugin : Plugin<Project> {
    private lateinit var mSkinDir: File
    private lateinit var mOutputDir: File
    override fun apply(project: Project) {
        // 创建SkinExtension扩展对象
        // 注意这里只是创建扩展对象, 只有当配置阶段执行完毕后配置对象才会被赋值
        val extensions = project.extensions.create("skin", SkinExtension::class.java)
        // 创建皮肤打包Task, 在配置阶段结束后创建, 这样才能拿到扩展中的值
        project.afterEvaluate {
            mSkinDir = File(extensions.skinPath)
            if (!mSkinDir.exists() || !mSkinDir.isDirectory) {
                errorln("$TAG${extensions.skinPath} is not a skin pack path")
                return@afterEvaluate
            }
            mOutputDir = extensions.outputPath?.let { File(it) } ?: File("${project.buildDir}/skins")
            createPackageSkinTask(project, mSkinDir)
        }
    }

    private fun createPackageSkinTask(project: Project, file: File) {
        if (!file.exists() || !file.isDirectory) {
            return
        }
        if (file.name == "res") {
            val parentFile = file.parentFile
            val taskName = "package${parentFile.name.replaceFirstChar { it.uppercaseChar() }}"
            val outputDir = File(mOutputDir, parentFile.name)
            project.tasks.register(taskName, PackageSkinTask::class.java) {
                it.group = "skin"
                it.description = "生成皮肤包."
                it.skinDir = parentFile
                it.outputDir = outputDir
            }
            return
        }

        file.listFiles()?.forEach {
            createPackageSkinTask(project, it)
        }
    }
}