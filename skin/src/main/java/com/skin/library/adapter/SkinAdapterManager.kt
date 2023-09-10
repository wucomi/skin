package com.skin.library.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.skin.library.common.SkinAttrUtils
import kotlin.reflect.KClass

object SkinAdapterManager {
    private val mAdapters = hashMapOf<KClass<out View>, ISkinAdapter>()

    init {
        addAdapter(
            View::class to ViewSkinAdapter(),
            TextView::class to TextViewSkinAdapter(),
            ImageView::class to ImageViewSkinAdapter(),
        )
        try {
            // 绑定自定义Adapter
            Class.forName("com.skin.library.adapter.CustomAdapterBinding")
                .getConstructor(SkinAdapterManager::class.java)
                .newInstance(this)
        } catch (_: Throwable) {
        }
    }

    fun getAdapters(view: View): List<ISkinAdapter> {
        //性能优化: 保存adapter, 下次直接获取
        val skinAdapters =
            SkinAttrUtils.getSkinAdapters(view) ?: mAdapters.filter { it.key.isInstance(view) }.values.toList()
        SkinAttrUtils.saveSkinAdapters(view, skinAdapters)
        return skinAdapters
    }

    fun addAdapter(vararg adapters: Pair<KClass<out View>, ISkinAdapter>) {
        mAdapters.putAll(adapters)
    }

    fun removeAdapter(type: KClass<out View>) {
        mAdapters.remove(type)
    }
}