package com.skin.app

import android.app.Application
import com.skin.library.SkinManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinManager.init(this)
    }
}