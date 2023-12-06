package com.hyphenate.chatroom

import android.app.Activity

interface ActivityState {
    /**
     * Current activity
     * @return
     */
    fun current(): Activity?

    /**
     * Activity list
     * @return
     */
    val activityList: List<Activity>

    /**
     * The size of the activity list
     * @return
     */
    fun count(): Int

    /**
     * Whether the application is in the foreground
     * @return
     */
    val isFront: Boolean
}