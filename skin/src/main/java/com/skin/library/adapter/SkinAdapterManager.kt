package com.skin.library.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.skin.library.common.SkinAttrUtils

object SkinAdapterManager {
    private val mAdapters = hashMapOf<Class<out View>, ISkinAdapter>()

    init {
        addAdapter(
            View::class.java to ViewSkinAdapter(),
            TextView::class.java to TextViewSkinAdapter(),
            ImageView::class.java to ImageViewSkinAdapter(),
        )
    }

    fun getAdapters(view: View): List<ISkinAdapter> {
        //性能优化: 保存adapter, 下次直接获取
        val skinAdapters =
            SkinAttrUtils.getSkinAdapters(view) ?: mAdapters.filter { it.key.isInstance(view) }.values.toList()
        SkinAttrUtils.saveSkinAdapters(view, skinAdapters)
        return skinAdapters
    }

    fun addAdapter(vararg adapters: Pair<Class<out View>, ISkinAdapter>) {
        mAdapters.putAll(adapters)
    }

    fun removeAdapter(type: Class<out View>) {
        mAdapters.remove(type)
    }
}