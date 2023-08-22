package com.skin.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skin.app.databinding.ActivitySettingBinding
import com.skin.library.SkinManager
import java.io.File

class SettingActivity : AppCompatActivity() {
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.defaultSkin.setOnClickListener {
            SkinManager.unloadAllSkin()
        }
        binding.main1.setOnClickListener {
            SkinManager.loadSkin(File(cacheDir, "main1.skin").absolutePath, "main")
        }
        binding.main2.setOnClickListener {
            SkinManager.loadSkin(File(cacheDir, "main2.skin").absolutePath, "main")
        }
        binding.setting1.setOnClickListener {
            SkinManager.loadSkin(File(cacheDir, "setting1.skin").absolutePath, "setting")
        }
    }
}