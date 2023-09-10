# Android动态换肤

#### 介绍

动态换肤

 ![.gif](https://s2.loli.net/2025/01/11/6NGuFQtCLe7iDEd.gif)

#### 软件架构

- **扩展性**：支持扩展自己的皮肤适配器，开发者可以轻松实现对其他自定义控件的换肤逻辑。而且父控件设置的adapter，会自动匹配其所有子控件。
- **精确控制**：支持App、Activity、View三种级别的换肤，开发者自己决定换肤层级，提升换肤性能。
- **兼容性**：不改变系统原有的布局解析逻辑，支持AppCompat库的布局解析。兼容 Android 5.0 (API 级别 21) 及以上版本，覆盖绝大多数主流 Android 设备。
- **易用性**：提供清晰、易懂的 API 接口，以及详尽的集成文档和示例代码。通过极少的API即可实现换肤操作。
- **性能**：项目对性能做了大量优化，资源热替换机制确保换肤过程迅速流畅，控件跟适配器绑定重新刷新UI可以直接拿到皮肤适配器。


#### 安装教程

在项目的 `setting.gradle` 文件中，添加三方库的依赖项。

```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

在项目module的 `build.gradle` 文件中，添加三方库的依赖项。

```groovy
plugins {
    id 'io.github.wurensen.android-aspectjx' version '3.3.2'
    id 'kotlin-kapt'
}

dependencies {
    implementation 'com.github.wucomi.skin:skin:1.1.0'
    kapt 'com.github.wucomi.skin:complier:1.1.0'
}
```




#### 使用说明

##### 初始化

初始化时可以传入自定义的适配器，适配器会自动适配指定View类型及其子类型。

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinManager.init(this)
    }
}
```

##### 自定义Adapter

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

##### 精确控制开启换肤

支持三种级别的开启换肤，View级别、Activity级别、App级别。

**View级别**

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

**Activity级别**

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

**App级别**

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

##### 动态设置属性

当我们在代码中动态设置资源ID时，需要使用SkinManager.getResources()去获取资源

```kotlin
binding.tv.setTextColor(SkinManager.getResources().getColor(R.color.text_color))
```

##### 加载皮肤包

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

##### 移出皮肤包

```groovy
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

🎉🎉🎉 重磅推荐！🎉🎉🎉 让我来隆重介绍一下这款超给力的自动打包插件。它会帮我们生成体积超小的皮肤包，让打包操作变得轻松又便捷！🚀🚀🚀。

![_20250111185420.png](https://s2.loli.net/2025/01/11/ufoBq7zjWJtw5cF.jpg)

使用插件会自动为我们生成皮肤包路径下的打包命令，执行命令会生成对应的皮肤包。

项目settings.gradle中添加

```groovy
pluginManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

module的build.gradle中添加

```groovy
// 皮肤打包插件
plugins {
    id 'io.github.wucomi.skin' version '1.0.0'
}
skin {
    // 皮肤所在路径
    skinPath file("./skins").absolutePath
    // 皮肤输出路径(可选, 默认为build/skins)
    outputPath file("${project.buildDir}/skins")
}
```

注意事项：

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
