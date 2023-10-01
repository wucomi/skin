package com.skin.library.resources

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes

interface IResources {
    fun <T> getResource(@AnyRes resId: Int?): T?
    fun getColor(@ColorRes resId: Int): Int
    fun getDrawable(@DrawableRes resId: Int): Drawable?
    fun getTypeface(@FontRes resId: Int): Typeface?
    fun getResourceTypeName(@AnyRes resId: Int): String?
}