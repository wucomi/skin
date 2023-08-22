package com.skin.library.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * 默认Resources
 */
class DefaultResources(private val context: Context) : IResources {
    private val mAsset = context.assets
    private val mResources = context.resources
    private val mPkg: PackageInfo

    init {
        val pm: PackageManager = context.packageManager
        mPkg = pm.getPackageInfo(context.packageName, 0)
    }

    override fun getColor(resId: Int): Int {
        return try {
            mResources.getColor(resId)
        } catch (_: Throwable) {
            0
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getDrawable(resId: Int): Drawable? {
        return try {
            mResources.getDrawable(resId)
        } catch (_: Throwable) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTypeface(resId: Int): Typeface? {
        return try {
            mResources.getFont(resId)
        } catch (_: Throwable) {
            null
        }
    }

    override fun getResourceTypeName(resId: Int): String? {
        return try {
            mResources.getResourceTypeName(resId)
        } catch (_: Throwable) {
            null
        }
    }
}