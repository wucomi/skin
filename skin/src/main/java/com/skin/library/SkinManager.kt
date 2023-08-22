package com.skin.library

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Trace
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import com.skin.library.adapter.ISkinAdapter
import com.skin.library.adapter.SkinAdapterManager
import com.skin.library.common.ActivityManager
import com.skin.library.common.SkinConfig
import com.skin.library.constants.SkinMetaData
import com.skin.library.resources.DefaultResources
import com.skin.library.resources.ExternalResources
import com.skin.library.resources.IResources
import com.skin.library.resources.MergeResources

const val TAG = "SKIN"

object SkinManager {
    private val mSkinLifecycleCallbacks = ActivityManager()
    private lateinit var mContext: Application
    private lateinit var mSkinConfig: SkinConfig

    //资源加载管理器
    private lateinit var resources: IResources

    //正在用的皮肤包的Type, path
    private var mSkinPathPairs = linkedMapOf<String, String>()

    //正在用的皮肤包的Type, Resources, 包括默认的Resources
    private var mSkinResourcesPairs = linkedMapOf<String, IResources>()

    //所有缓存过的皮肤包的Resources
    private var mSkinResourcesPairsCache = linkedMapOf<String, IResources>()

    //Activity是否允许换肤
    private val mActivityOpenSkin = hashMapOf<String, Boolean?>()

    //App是否允许换肤
    var isAppOpenSkin = false
        private set

    /**
     * 是否是默认皮肤
     */
    val isDefaultSkin: Boolean
        get() = mSkinResourcesPairs.isEmpty()

    /**
     * 初始化
     * @param app Application
     * @param adapter View类型 to 皮肤适配器
     */
    fun init(app: Application, vararg adapter: Pair<Class<out View>, ISkinAdapter>) {
        mContext = app
        //获取是否开启换肤
        val packageManager = app.packageManager
        val metaDataPackageInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_META_DATA)
        isAppOpenSkin = metaDataPackageInfo.applicationInfo.metaData?.getBoolean(SkinMetaData.OPEN_SKIN) ?: false
        val activitiesPackageInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES)
        activitiesPackageInfo.activities?.forEach {
            val componentName = ComponentName(app.packageName, it.name)
            val activityInfo = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            mActivityOpenSkin[activityInfo.name] = activityInfo.metaData?.getBoolean(SkinMetaData.OPEN_SKIN)
        }

        app.registerActivityLifecycleCallbacks(mSkinLifecycleCallbacks)
        resources = MergeResources(mSkinResourcesPairs, DefaultResources(app))

        //添加自定义皮肤适配器
        SkinAdapterManager.addAdapter(*adapter)
        //加载上次的皮肤包
        mSkinConfig = SkinConfig(app)
        val skinPaths = mSkinConfig.skinPaths
        skinPaths?.forEach {
            loadSkinInternal(it.value, it.key)
        }
    }

    /**
     * 加载皮肤包
     * @param path 皮肤包路径
     * @param type 皮肤包类型
     */
    fun loadSkin(path: String, type: String = "default") {
        loadSkinInternal(path, type)
        //立即换肤
        applySkin()
        //保存加载的皮肤包
        mSkinConfig.skinPaths = mSkinPathPairs
    }

    private fun loadSkinInternal(path: String, type: String) {
        if (mSkinPathPairs[type] == path) {
            //正在使用的皮肤包跳过
            return
        }
        mSkinPathPairs[type] = path
        val cacheSkinResources = mSkinResourcesPairsCache[path]
        if (cacheSkinResources !== null) {
            // 加载过的皮肤包直接从缓存中取
            mSkinResourcesPairs[type] = cacheSkinResources
        } else {
            //加载皮肤
            try {
                Trace.beginSection("loadSkin")
                val externalResources = ExternalResources(mContext, path)
                mSkinResourcesPairs[path] = externalResources
                mSkinResourcesPairsCache[path] = externalResources
            } catch (e: Throwable) {
                Log.e(TAG, "loadSkin: 加载皮肤失败, path:${path}", e)
                e.printStackTrace()
            } finally {
                Trace.endSection()
            }
        }
    }

    /**
     * 移出皮肤包
     * @param type 皮肤包类型
     * @param always 永久移出
     */
    fun unloadSkin(type: String, always: Boolean = false) {
        val path = mSkinPathPairs.remove(type) ?: return
        mSkinResourcesPairs.remove(type)
        if (always) {
            mSkinResourcesPairsCache.remove(path)
        }
        applySkin()
        //保存加载的皮肤包
        mSkinConfig.skinPaths = mSkinPathPairs
    }

    /**
     * 移出所有皮肤包
     * @param always 永久移出
     */
    fun unloadAllSkin(always: Boolean = false) {
        mSkinPathPairs.clear()
        mSkinResourcesPairs.clear()
        if (always) {
            mSkinResourcesPairs.keys.forEach {
                mSkinResourcesPairsCache.remove(it)
            }
        }
        applySkin()
        //保存加载的皮肤包
        mSkinConfig.skinPaths = mSkinPathPairs
    }

    /**
     * 配置动态换肤解析工厂
     * 注意要在Activity的super.onCreate方法之前调用该方法
     */
    fun setFactory2(target: Activity) {
        if (target is AppCompatActivity) {
            val layoutInflater = LayoutInflater.from(target)
            LayoutInflaterCompat.setFactory2(layoutInflater, object : SkinFactory2(layoutInflater) {
                override fun createView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
                    return target.getDelegate().createView(parent, name, context, attrs)
                }
            })
        } else {
            val layoutInflater = LayoutInflater.from(target)
            layoutInflater.factory2 = object : SkinFactory2(layoutInflater) {
                override fun createView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
                    return null
                }
            }
        }
    }

    /**
     * 获取资源解析器
     */
    fun getResources(): IResources {
        return resources
    }

    /**
     * Activity是否开启了换肤
     */
    fun isActivityOpenSkin(activityName: String): Boolean? {
        return mActivityOpenSkin[activityName]
    }

    //全量换肤
    private fun applySkin() {
        try {
            Trace.beginSection("applySkin")
            //优先加载当前显示的Activity
            mSkinLifecycleCallbacks.getAllActivity().reversed().forEach {
                it.recreate()
            }
        } finally {
            Trace.endSection()
        }
    }
}