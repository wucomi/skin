package com.skin.library.common

import android.view.View
import com.skin.library.R
import com.skin.library.adapter.ISkinAdapter

object SkinAttrUtils {
    @Suppress("UNCHECKED_CAST")
    fun getSkinAttrs(view: View): HashMap<String, Int> {
        return view.getTag(R.id.skin_attrs) as? HashMap<String, Int> ?: hashMapOf()
    }

    fun saveSkinAttrs(view: View, attrs: Map<String, Int>) {
        val skinAttrs = getSkinAttrs(view)
        skinAttrs.putAll(attrs)
        view.setTag(R.id.skin_attrs, skinAttrs)
    }

    fun saveSkinAttrs(view: View, vararg attrs: Pair<String, Int>) {
        saveSkinAttrs(view, attrs.toMap())
    }

    @Suppress("UNCHECKED_CAST")
    fun getSkinAdapters(view: View): List<ISkinAdapter>? {
        return view.getTag(R.id.skin_adapters) as? List<ISkinAdapter>
    }

    fun saveSkinAdapters(view: View, adapters: List<ISkinAdapter>) {
        view.setTag(R.id.skin_adapters, adapters)
    }
}