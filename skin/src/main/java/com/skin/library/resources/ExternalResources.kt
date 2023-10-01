package com.skin.library.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * 外部Resources
 */
class ExternalResources(context: Context, path: String) : PResources() {
    // 默认Resources
    private val mResources = context.resources

    // 外部AssetManager
    // 默认的AssetManager单例只能加载应用内的资源，要加载应用外的资源我们需要创建一个新的AssetManager
    private val mExternalAsset: AssetManager = AssetManager::class.java.newInstance()

    // 外部Resources
    private val mExternalResources: Resources

    // 外部包信息
    private val mExternalPkgInfo: PackageInfo

    init {
        // 添加资源的路径
        val addAssetPath = mExternalAsset.javaClass.getMethod("addAssetPath", String::class.java)
        addAssetPath.invoke(mExternalAsset, path)
        // 根据当前的设备显示器信息与配置(横竖屏、语言等)创建外部Resources
        mExternalResources = Resources(mExternalAsset, mResources.displayMetrics, mResources.configuration)
        // 根据路径获取信息
        val pm: PackageManager = context.packageManager
        mExternalPkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
            ?: throw NotFoundException()
    }

    override fun getColor(resId: Int): Int {
        val externalResId = getIdentifier(resId)
        if (externalResId == 0) {
            return 0
        }
        return mExternalResources.getColor(externalResId)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getDrawable(resId: Int): Drawable? {
        val externalResId = getIdentifier(resId)
        if (externalResId == 0) {
            return null
        }
        return mExternalResources.getDrawable(externalResId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTypeface(resId: Int): Typeface? {
        val externalResId = getIdentifier(resId)
        if (externalResId == 0) {
            return null
        }
        return mExternalResources.getFont(externalResId)
    }

    override fun getResourceTypeName(resId: Int): String? {
        return try {
            mResources.getResourceTypeName(resId)
        } catch (_: Throwable) {
            null
        }
    }

    //获取外部资源ID
    private fun getIdentifier(resId: Int): Int {
        val resName: String = mResources.getResourceEntryName(resId)
        val resType: String = mResources.getResourceTypeName(resId)
        return try {
            mExternalResources.getIdentifier(resName, resType, mExternalPkgInfo.packageName)
        } catch (_: Throwable) {
            0
        }
    }

}