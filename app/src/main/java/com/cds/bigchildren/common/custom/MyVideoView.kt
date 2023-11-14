package com.cds.bigchildren.common.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class MyVideoView(context: Context?, attrs: AttributeSet?) : VideoView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = getDefaultSize(0,widthMeasureSpec)
        val height = getDefaultSize(0,heightMeasureSpec)
        setMeasuredDimension(width,height)
    }
}