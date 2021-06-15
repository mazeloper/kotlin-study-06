package com.jschoi.develop.aop_part03_chapter08

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

/**
 * Custom Motion Layout 작성이유
 *
 * 모션레이아웃을 제외한 다른 뷰에 터치,클릭 이벤트가 먹지 않는다.
 */
class CustomVideoMotionLayout(context: Context, attributeSet: AttributeSet? = null) :
    MotionLayout(context, attributeSet) {

    init {
        setTransitionListener(object : TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
    }

    private var motionTouchStarted = false
    private val mainContainerLayout: View by lazy {
        findViewById(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    private val gestureListener by lazy {
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {

                mainContainerLayout.getHitRect(hitRect)
                return hitRect.contains(e1.x.toInt(), e1.y.toInt())
            }
        }
    }
    private val gestureDetector by lazy {
        GestureDetector(context, gestureListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (motionTouchStarted.not()) {
            mainContainerLayout.getHitRect(hitRect)
            // 모션레이아웃 안에서 일어난 이벤트이냐
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
            Log.d("TAG", "모션레이아웃에서 이벤트가 발생 ${if(motionTouchStarted) "하였습니다" else "되지않았습니다"}")
        }
        return super.onTouchEvent(event) && motionTouchStarted
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}