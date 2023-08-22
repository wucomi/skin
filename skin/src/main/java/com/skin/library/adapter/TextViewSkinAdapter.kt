package com.skin.library.adapter

import android.view.View
import android.widget.TextView
import com.skin.library.constants.SkinAttrs
import com.skin.library.resources.IResources

class TextViewSkinAdapter : ISkinAdapter {

    override fun applySkin(view: View, resources: IResources, attrs: HashMap<String, Int>) {
        val textView = view as TextView
        setTextColor(textView, resources, attrs[SkinAttrs.textColor])
        setFontFamily(textView, resources, attrs[SkinAttrs.fontFamily])
        val drawableLeft =
            attrs[SkinAttrs.drawableStartCompat]
                ?: attrs[SkinAttrs.drawableLeftCompat]
                ?: attrs[SkinAttrs.drawableStart]
                ?: attrs[SkinAttrs.drawableLeft]
        val drawableTop =
            attrs[SkinAttrs.drawableTopCompat]
                ?: attrs[SkinAttrs.drawableTop]
        val drawableRight =
            attrs[SkinAttrs.drawableEndCompat]
                ?: attrs[SkinAttrs.drawableRightCompat]
                ?: attrs[SkinAttrs.drawableEnd]
                ?: attrs[SkinAttrs.drawableRight]
        val drawableBottom =
            attrs[SkinAttrs.drawableBottomCompat]
                ?: attrs[SkinAttrs.drawableBottom]
        setDrawable(textView, resources, listOf(drawableLeft, drawableTop, drawableRight, drawableBottom))
    }

    private fun setTextColor(textView: TextView, resources: IResources, resId: Int?) {
        if (resId != null) {
            textView.setTextColor(resources.getColor(resId))
        }
    }

    private fun setFontFamily(textView: TextView, resources: IResources, resId: Int?) {
        if (resId != null) {
            textView.typeface = resources.getTypeface(resId)
        }
    }

    private fun setDrawable(textView: TextView, resources: IResources, drawableRedIds: List<Int?>) {
        val drawables = drawableRedIds.map { resId ->
            if (resId == null) {
                null
            } else {
                resources.getDrawable(resId)
            }
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3])
    }
}