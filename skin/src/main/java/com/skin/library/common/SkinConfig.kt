package com.skin.library.common

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

const val SKIN_CONFIG = "SKIN_CONFIG"
const val SKIN_CONFIG_KEY_PATHS = "SKIN_CONFIG_KEY_PATHS"

class SkinConfig(private val context: Application) {
    private val mSp: SharedPreferences = context.getSharedPreferences(SKIN_CONFIG, Context.MODE_PRIVATE)

    var skinPaths: Map<String, String>?
        get() {
            val jsonStr = mSp.getString(SKIN_CONFIG_KEY_PATHS, null) ?: return null
            val jsonObject = JSONObject(jsonStr)
            val result = hashMapOf<String, String>()
            jsonObject.keys().forEach {
                result[it] = jsonObject.getString(it)
            }
            return result
        }
        set(value) {
            val editor = mSp.edit()
            if (value == null) {
                editor.remove(SKIN_CONFIG_KEY_PATHS)
            } else {
                val jsonObject = JSONObject()
                value.forEach {
                    jsonObject.put(it.key, it.value)
                }
                editor.putString(SKIN_CONFIG_KEY_PATHS, jsonObject.toString())
            }
            editor.apply()
        }
}