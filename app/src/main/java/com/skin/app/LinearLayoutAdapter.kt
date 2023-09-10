package com.skin.app

import android.view.View
import com.skin.annotations.Adapter
import com.skin.library.adapter.ISkinAdapter
import com.skin.library.constants.SkinAttrs
import com.skin.library.resources.IResources

@Adapter("android.widget.LinearLayout")
class LinearLayoutAdapter : ISkinAdapter {
    override fun applySkin(view: View, resources: IResources, attrs: HashMap<String, Int>) {
        setBackground(view, resources, attrs[SkinAttrs.background])
    }

    private fun setBackground(view: View, resources: IResources, resId: Int?) {
        if (resId != null) {
            val typeName = resources.getResourceTypeName(resId)
            if (typeName === "color") {
                view.setBackgroundColor(resources.getColor(resId))
            } else {
                view.setBackgroundDrawable(resources.getDrawable(resId))
            }
        }
    }
}