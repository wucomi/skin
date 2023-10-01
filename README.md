# Android动态换肤

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

#### 介绍

🌈🌈🌈一个几乎零耦合的动态换肤框架🌈🌈🌈，这款框架的超酷的亮点如下：

1. **即时性**：用户可以即时切换皮肤，无需重启应用。
2. **灵活性**：支持App、Activity、View三种级别的换肤。
3. **易用性**：无论是XML布局还是代码中，都能轻松实现资源的动态设置。
4. **扩展性**：开发者可以轻松新增皮肤适配器，实现自定义控件的换肤逻辑。
5. **继承性**：为父类控件配置适配器时，该适配器会自动应用于其所有子类控件。
6. **兼容性**：兼容主流组件库，包括 AppCompat 以及 support v4、v7 组件库。
7. **组合性**：皮肤包支持拆分，可单独更新特定模块的皮肤，多模块皮肤包可同时展示。

   <img src="https://s2.loli.net/2025/01/11/6NGuFQtCLe7iDEd.gif" alt="示例图片" width="320" height="600" style="display: inline-block;">

   赶紧来试试这款超棒的换肤框架吧！🚀🚀🚀

#### 安装教程

1. 在项目的 `setting.gradle` 文件中，添加三方库的依赖项。

    ```groovy
    dependencyResolutionManagement {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
    ```

2. 在项目module的 `build.gradle` 文件中，添加三方库的依赖项。

    ```groovy
    plugins {
        id 'io.github.wurensen.android-aspectjx' version '3.3.2'
        id 'kotlin-kapt'
    }
    
    dependencies {
        implementation 'com.github.wucomi.skin:skin:Tag'
        kapt 'com.github.wucomi.skin:compiler:Tag'
    }
    ```




#### 使用说明

1. 初始化

    初始化时可以传入自定义的适配器，适配器会自动适配指定View类型及其子类型。

    ```kotlin
    class App : Application() {
        override fun onCreate() {
            super.onCreate()
            SkinManager.init(this)
        }
    }
    ```

2. 自定义Adapter

    如果项目中有自定义的组件需要支持换肤，可以自定义adapter来实现自动换肤，以下以 `LinearLayout` 为例进行说明：
    
    ```kotlin
    @Adapter("android.widget.LinearLayout")
    class LinearLayoutAdapter : ISkinAdapter {
        override fun applySkin(view: View, resources: IResources, attrs: HashMap<String, Int>) {
            setBackground(view, resources, attrs[SkinAttrs.background])
        }
    
        private fun setBackground(view: View, resources: IResources, resId: Int?) {
            if (resId != null) {
                val typeName = resources.getResourceTypeName(resId)
                if (typeName === "color") {
                    view.setBackgroundColor(resources.getColor(resId))
                } else {
                    view.setBackgroundDrawable(resources.getDrawable(resId))
                }
            }
        }
    }
    ```

3. 开启换肤

    支持三种级别的开启换肤，View级别、Activity级别、App级别。
    
    - View级别
    
    当View级别设置了换肤开启或关闭以View级别设置的为准。
    
    ```xml
    <!-- View级别开启换肤 -->
    <LinearLayout
        xmlns:skin="http://schemas.android.com/apk/res-auto"
        skin:enable="true"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
    </LinearLayout>
    ```
    
    - Activity级别
    
    当View级别没有设置换肤开启属性，以Activity级别设置的为准。
    
    ```xml
    <activity
        android:name=".SettingActivity"
        android:exported="true">
        <!-- Activity级别开启换肤 -->
        <meta-data
            android:name="open_skin"
            android:value="true" />
    </activity>
    ```
    
    - App级别
    
    如果上面两个级别都没有配置换肤开启属性，则以App级别的为准，App级别不配置默认不开启换肤
    
    ```xml
    <application
        android:name=".App"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.ComponentApp">
        <!-- App级别开启换肤 -->
        <meta-data
            android:name="open_skin"
            android:value="false" />
    </application>
    ```
4. 动态设置属性

    当我们在代码中动态设置资源ID时，需要使用SkinManager.getResources()去获取资源
    
    ```kotlin
    binding.tv.setTextColor(r(R.color.text_color))
    ```
    
5. 加载皮肤包
   
    ```kotlin
    binding.main1.setOnClickListener {
        // 加载main模块的皮肤包为main1.skin，不能和main模块的皮肤包共存
        SkinManager.loadSkin(File(cacheDir, "main1.skin").absolutePath, "main")
    }
    binding.main2.setOnClickListener {
        // 加载main模块的皮肤包为main2.skin，不能和main模块的皮肤包共存
        SkinManager.loadSkin(File(cacheDir, "main2.skin").absolutePath, "main")
    }
    binding.setting1.setOnClickListener {
        // 加载setting模块的皮肤包setting1，可以和main模块的皮肤包共存
        SkinManager.loadSkin(File(cacheDir, "setting1.skin").absolutePath, "setting")
    }
    ```

6. 移出皮肤包

    ```kotlin
    binding.defaultSkin.setOnClickListener {
        // 移出所有皮肤包，还原为默认皮肤
        SkinManager.unloadAllSkin()
    }
    binding.removeMain.setOnClickListener {
        // 移出main模块的皮肤包
        SkinManager.unloadSkin("main")
    }
    ```



#### 皮肤打包插件

🎉🎉🎉 重磅推荐！🎉🎉🎉 让我来隆重介绍一下这款超给力的自动打包插件。它会帮我们生成体积超小的皮肤包，让打包操作变得轻松又便捷！🚀🚀🚀。使用插件会自动为我们生成皮肤包路径下的打包命令，执行命令会生成对应的皮肤包。

<img src="https://s2.loli.net/2025/01/11/ufoBq7zjWJtw5cF.jpg" alt="打包命令" style="max-height:500px; max-width:90%; display: inline-block; margin-left:20px;">

下面是插件的安装步骤: 

1. 项目settings.gradle中添加

    ```groovy
    pluginManagement {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
    ```

2. module的build.gradle中添加

    ```groovy
    // 皮肤打包插件
    plugins {
        id 'io.github.wucomi.skin' version Tag
    }
    skin {
        // 皮肤所在路径
        skinPath file("./skins").absolutePath
        // 皮肤输出路径(可选, 默认为build/skins)
        outputPath file("${project.buildDir}/skins")
    }
    ```

3. 注意事项：

    皮肤包的AndroidManifast中一定要配置包名。

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <manifest package="com.skin.main1" />
    ```



#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
