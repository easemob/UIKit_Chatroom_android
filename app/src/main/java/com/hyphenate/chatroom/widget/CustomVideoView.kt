package com.hyphenate.chatroom.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class CustomVideoView: VideoView {
    private var mVideoWidth = 0
    private var mVideoHeight = 0

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mVideoWidth, mVideoHeight)
    }

    /**
     * User can set custom video width and height.
     */
    fun setVideoSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
        requestLayout()
        invalidate()
    }

}