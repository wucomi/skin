package com.skin.library.resources

abstract class PResources : IResources {
    @Suppress("UNCHECKED_CAST")
    override fun <T> getResource(resId: Int?): T? {
        if (resId == null) {
            return null
        }
        return when (getResourceTypeName(resId)) {
            "color" -> getColor(resId) as? T
            "drawable" -> getDrawable(resId) as? T
            "typeface" -> getTypeface(resId) as? T
            else -> null
        }
    }
}