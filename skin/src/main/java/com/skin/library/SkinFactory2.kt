package com.skin.library

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import com.skin.library.adapter.SkinAdapterManager
import com.skin.library.constants.Namespace.SKIN_ATTR_ENABLE
import com.skin.library.constants.Namespace.SKIN_NAMESPACE

/**
 * View创建工厂
 *
 * 所有使用LayoutInflater.inflate进行布局解析的场景，都会触发布局解析器的工作流程，包括但不限于：
 * - Activity的setContentView(layoutResID)
 * - Fragment的XML布局加载
 * - RecyclerView.ViewHolder动态加载的XML布局
 *
 * 当LayoutInflater布局解析器完成XML解析后，将通过Factory2来创建View实例。
 *
 * 利用该切面，可以对动态新加载的布局进行换肤处理，调整View的样式，如背景色、字体颜色等，以实现应用的动态换肤效果
 *
 * 注意：对于通过代码直接实例化创建的View（非从XML布局文件中加载），布局解析器不会介入，这些View需要单独进行换肤处理。
 */
abstract class SkinFactory2(private val layoutInflater: LayoutInflater) : Factory2 {
    abstract fun createView(parent: View?, name: String, context: Context, attrs: AttributeSet): View?

    final override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        var view: View? = createView(parent, name, context, attrs)
        if (view == null) {
            try {
                //android.widget.LinearLayout
                view = layoutInflater.createView(name, "android.widget.", attrs)
            } catch (_: Throwable) {
            }
        }
        if (view == null) {
            try {
                //androidx.recyclerview.widget.RecyclerView
                view = layoutInflater.createView(name, null, attrs)
            } catch (_: Throwable) {
            }
        }
        if (view == null) {
            try {
                //android.view.TextView
                view = layoutInflater.createView(name, "android.view.", attrs)
            } catch (_: Throwable) {
            }
        }
        if (view == null) {
            try {
                //android.webkit.WebView
                view = layoutInflater.createView(name, "android.webkit.", attrs)
            } catch (_: Throwable) {
            }
        }

        //上面按照系统源码逻辑尽量创建出所有的View，这样就能改变所有相关View的皮肤
        //性能优化: 如果现在没有加载皮肤包新加载的布局不用执行换肤逻辑
        if (view !== null && !SkinManager.isDefaultSkin) {
            //换肤
            changeSkin(view, attrs)
        }
        return view
    }

    final override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    //换肤
    private fun changeSkin(view: View, attrs: AttributeSet) {
        val skinAdapters = SkinAdapterManager.getAdapters(view)
        if (skinAdapters.isEmpty()) {
            return
        }
        //View设置了以View为准, View没设置, Activity设置了以Activity为准, 都没设置以App为准
        val enableSkin =
            attrs.getAttributeValue(SKIN_NAMESPACE, SKIN_ATTR_ENABLE)?.toBoolean()
                ?: SkinManager.isActivityOpenSkin(view.context.toString().split("@")[0])
                ?: SkinManager.isAppOpenSkin
        //没有打开换肤不进行换肤
        if (!enableSkin) return

        // 获取属性
        val attrsMap = hashMapOf<String, Int>()
        for (i in 0 until attrs.attributeCount) {
            //获得属性名 textColor/background
            val attributeName = attrs.getAttributeName(i)
            //获取属性值 #FF0000布局中写死的  ?666666666系统私有资源Id  @888888888应用资源Id
            val attributeValue = attrs.getAttributeValue(i)

            if (attributeName == "id") {
                // id属性跳过
                continue
            }
            if ((!attributeValue.startsWith("@")) || attributeValue.length < 2) {
                // 布局中写死的和系统私有的跳过
                continue
            }

            val attributeResId = attributeValue.substring(1).toInt()
            if (attributeResId == 0) {
                // 资源ID是0的跳过
                continue
            }

            attrsMap[attributeName] = attributeResId
        }

        // 更新皮肤
        skinAdapters.forEach {
            it.applySkin(view, SkinManager.resources, attrsMap)
        }
    }
}