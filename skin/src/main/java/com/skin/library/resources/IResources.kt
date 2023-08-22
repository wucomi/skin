package com.skin.library.resources

import android.graphics.Typeface
import android.graphics.drawable.Drawable

interface IResources {
    fun getColor(resId: Int): Int
    fun getDrawable(resId: Int): Drawable?
    fun getTypeface(resId: Int): Typeface?
    fun getResourceTypeName(resId: Int): String?
}