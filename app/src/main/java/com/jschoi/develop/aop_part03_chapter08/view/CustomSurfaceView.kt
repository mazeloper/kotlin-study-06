package com.jschoi.develop.aop_part03_chapter08.view

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView

class CustomSurfaceView(context: Context, attrs: AttributeSet? = null) :
    SurfaceView(context, attrs) {

    var fragment: PlayerAdvanceFragment? = null

    fun inject(fragment: PlayerAdvanceFragment) {
        this.fragment = fragment
    }
}