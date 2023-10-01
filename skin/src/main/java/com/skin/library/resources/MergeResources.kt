package com.skin.library.resources

import android.graphics.Typeface
import android.graphics.drawable.Drawable

/**
 * 混合Resources
 */
class MergeResources(
    private val resourcesPairs: LinkedHashMap<String, IResources>,
    private val defaultResources: IResources
) : PResources() {
    override fun getColor(resId: Int): Int {
        var color = 0
        resourcesPairs.forEach {
            val color1 = it.value.getColor(resId)
            if (color1 != 0) {
                color = color1
            }
        }
        if (color == 0) {
            color = defaultResources.getColor(resId)
        }
        return color
    }

    override fun getDrawable(resId: Int): Drawable? {
        var drawable: Drawable? = null
        resourcesPairs.forEach {
            val drawable1 = it.value.getDrawable(resId)
            if (drawable1 != null) {
                drawable = drawable1
            }
        }
        if (drawable == null) {
            drawable = defaultResources.getDrawable(resId)
        }
        return drawable
    }

    override fun getTypeface(resId: Int): Typeface? {
        var typeface: Typeface? = null
        resourcesPairs.forEach {
            val typeface1 = it.value.getTypeface(resId)
            if (typeface1 != null) {
                typeface = typeface1
            }
        }
        if (typeface == null) {
            typeface = defaultResources.getTypeface(resId)
        }
        return typeface
    }

    override fun getResourceTypeName(resId: Int): String? {
        return defaultResources.getResourceTypeName(resId)
    }
}