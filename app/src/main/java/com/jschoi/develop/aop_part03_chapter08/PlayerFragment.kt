package com.jschoi.develop.aop_part03_chapter08

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import com.jschoi.develop.aop_part03_chapter08.databinding.FragmentPlayerBinding
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player) {

    // TODO dummy video -
    private var binding: FragmentPlayerBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        fragmentPlayerBinding.playerMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding?.let {
                    // 프래그먼트 붙힌 액티비티
                    (activity as MainActivity).also { mainActivity ->
                        // 프래그먼트 모션레이아웃 움직일때 메인모션레이아웃도 같이 이동하게
                        mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress =
                            abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}