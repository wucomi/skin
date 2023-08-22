package com.skin.library.common

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

/**
 * Activity生命周期AOP
 */
class ActivityManager : ActivityLifecycleCallbacks {
    private val mActivityList = arrayListOf<Activity>()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mActivityList.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        mActivityList.remove(activity)
    }

    fun getAllActivity(): List<Activity> {
        return mActivityList
    }
}