package com.skin.library.adapter

import android.view.View
import com.skin.library.resources.IResources

interface ISkinAdapter {
    fun applySkin(view: View, resources: IResources, attrs: HashMap<String, Int>)
}