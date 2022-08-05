package com.jschoi.develop.aop_part03_chapter08

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.jschoi.develop.aop_part03_chapter08.view.MainActivity
import kotlin.math.abs

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

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                // 프래그먼트 붙힌 액티비티
                (context as MainActivity).also { mainActivity ->
                    // 프래그먼트 모션레이아웃 움직일때 메인모션레이아웃도 같이 이동하게
                    mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress =
                        abs(progress)
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                ADLog.debug("onTransitionCompleted")
                motionTouchStarted = false
            }
        })
    }

    var motionTouchStarted = false
    private val mainContainerLayout: View by lazy { findViewById(R.id.mainContainerLayout) }
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (motionTouchStarted.not()) {
            mainContainerLayout.getHitRect(hitRect)
            ADLog.information("\nrect : $hitRect\nX : ${event.x.toInt()}\nY:  ${event.y.toInt()}")
            // 모션레이아웃 안에서 일어난 이벤트이냐
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
            Log.d("TAG", "모션레이아웃에서 이벤트가 발생 ${if (motionTouchStarted) "하였습니다" else "되지않았습니다"}")
        }
        return super.onTouchEvent(event) && motionTouchStarted
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}