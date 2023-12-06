package com.hyphenate.chatroom

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log

/**
 * Used for monitoring the activity lifecycle
 */
class UserActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks,
    com.hyphenate.chatroom.ActivityState {
    override val activityList: MutableList<Activity> = arrayListOf()
    private val resumeActivity: MutableList<Activity> = arrayListOf()
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        Log.d("ActivityLifecycle", "onActivityCreated " + activity.localClassName)
        activityList.add(0, activity)
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d("ActivityLifecycle", "onActivityStarted " + activity.localClassName)
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(
            "ActivityLifecycle",
            "onActivityResumed activity's taskId = " + activity.taskId + " name: " + activity.localClassName
        )
        if (!resumeActivity.contains(activity)) {
            resumeActivity.add(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d("ActivityLifecycle", "onActivityPaused " + activity.localClassName)
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d("ActivityLifecycle", "onActivityStopped " + activity.localClassName)
        resumeActivity.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
        Log.d("ActivityLifecycle", "onActivitySaveInstanceState " + activity.localClassName)
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d("ActivityLifecycle", "onActivityDestroyed " + activity.localClassName)
        activityList.remove(activity)
    }

    override fun current(): Activity? {
        return if (activityList.size > 0) activityList[0] else null
    }

    override fun count(): Int {
        return activityList.size
    }

    override val isFront: Boolean
        get() = resumeActivity.size > 0

    /**
     * Skip to the target activity
     * @param cls
     */
    fun skipToTarget(cls: Class<*>?) {
        if (activityList.size > 0) {
            current()?.startActivity(Intent(current(), cls))
            for (activity in activityList) {
                activity.finish()
            }
        }
    }

    /**
     * finish target activity
     * @param cls
     */
    fun finishTarget(cls: Class<*>) {
        if (activityList.isNotEmpty()) {
            for (activity in activityList) {
                if (activity.javaClass == cls) {
                    activity.finish()
                }
            }
        }
    }

    /**
     * Finish all activities
     */
    fun finishAll() {
        if (activityList.isNotEmpty()) {
            for (activity in activityList) {
                if (activity is ChatroomListActivity) {
                    continue
                }
                activity.finish()
            }
        }
    }

    val isOnForeground: Boolean
        /**
         * To judge whether the application is in the foreground
         * @return
         */
        get() = resumeActivity.isNotEmpty()

}