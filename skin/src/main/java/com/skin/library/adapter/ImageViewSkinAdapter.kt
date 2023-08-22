package com.skin.library.adapter

import android.view.View
import android.widget.ImageView
import com.skin.library.constants.SkinAttrs
import com.skin.library.resources.IResources

class ImageViewSkinAdapter : ISkinAdapter {
    override fun applySkin(view: View, resources: IResources, attrs: HashMap<String, Int>) {
        val imageView = view as ImageView
        setSrc(imageView, resources, attrs[SkinAttrs.src])
    }

    private fun setSrc(imageView: ImageView, resources: IResources, resId: Int?) {
        if (resId != null) {
            imageView.setImageDrawable(resources.getDrawable(resId))
        }
    }
}