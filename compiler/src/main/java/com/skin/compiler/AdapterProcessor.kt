package com.skin.compiler

import com.google.auto.service.AutoService
import com.skin.annotations.Adapter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind
import javax.tools.Diagnostic.Kind.ERROR

@AutoService(Processor::class)
//支持的注解
@SupportedAnnotationTypes("com.skin.annotations.Adapter")
//java版本号
@SupportedSourceVersion(SourceVersion.RELEASE_11)
class AdapterProcessor : AbstractProcessor() {

    /**
     * 注解处理的核心方法
     *
     * @param set 支持的注解类型
     * @param roundEnv 查找注解
     * @return 返回true为表示后续处理器不会再处理; 返回false表示仍可被其他处理器处理
     */
    override fun process(set: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (set.isEmpty()) {
            return false
        }

        val elements = roundEnv.getElementsAnnotatedWith(Adapter::class.java)

        // 定义类的包名
        val packageName = "com.skin.library.adapter"
        // 定义类的名称
        val className = "CustomAdapterBinding"
        // SkinAdapterManager类名
        val skinAdapterManagerTypeName = ClassName("com.skin.library.adapter", "SkinAdapterManager")

        // 创建类构建器，指定类的名称
        val classBuilder = TypeSpec.classBuilder(className)
            // 创建主构造函数，添加一个参数 skinAdapterManager，类型为 Any
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("skinAdapterManager", skinAdapterManagerTypeName)
                    .build()
            )
            // 添加一个私有属性 skinAdapterManager，类型为 Any，并初始化为构造函数传入的参数
            .addProperty(
                PropertySpec.builder("skinAdapterManager", skinAdapterManagerTypeName)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("skinAdapterManager")
                    .build()
            )
            // 添加初始化块，调用 skinAdapterManager 的 addAdapter 方法
            .addInitializerBlock(
                CodeBlock.builder()
                    .addStatement(
                        "skinAdapterManager.addAdapter(${
                            getCustomAdapters(elements)
                        })"
                    )
                    .build()
            )
            // 构建类
            .build()

        // 创建文件构建器，指定包名和文件名
        val fileBuilder = FileSpec.builder(packageName, className)
            // 将类添加到文件中
            .addType(classBuilder)
            // 构建文件
            .build()

        // 生成文件
        try {
            fileBuilder.writeTo(processingEnv.filer)
        } catch (e: Throwable) {
            logger(ERROR, "Failed to write CustomAdapterBinding class: ${e.message}")
        }
        return true
    }

    private fun getCustomAdapters(elements: MutableSet<out Element>): String {
        return elements.joinToString { element ->
            val adapterAnnotation = element.getAnnotation(Adapter::class.java)
            val viewClass = adapterAnnotation.value
            val adapterClass = element.asType().toString()
            " ${viewClass}::class to ${adapterClass}()"
        }
    }

    //打印日志
    private fun logger(level: Kind, msg: String) {
        processingEnv.messager.printMessage(level, msg)
    }
}