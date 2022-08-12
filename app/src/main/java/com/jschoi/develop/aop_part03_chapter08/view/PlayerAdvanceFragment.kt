package com.jschoi.develop.aop_part03_chapter08.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import com.jschoi.develop.aop_part03_chapter08.ADLog
import com.jschoi.develop.aop_part03_chapter08.R
import com.jschoi.develop.aop_part03_chapter08.databinding.FragmentPlayerBinding

class PlayerAdvanceFragment : Fragment(R.layout.fragment_player) {

    private lateinit var binding: FragmentPlayerBinding
    private var mSurfaceView: CustomSurfaceView? = null
    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSurface()
        initPlayer()
        initViews()
    }

    private fun initSurface() {
        mSurfaceView = CustomSurfaceView(requireContext())
        mSurfaceView?.inject(this)
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(requireContext()).build().apply {
            setVideoSurfaceView(mSurfaceView)
            addListener(exoListener())
        }
        binding.playerView.player = player
    }

    private fun initViews() {
        binding.playerView.setOnClickListener {

        }
    }

    // 플레이어 리스너
    private fun exoListener() = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            ADLog.debug("onVideoSizeChanged : ${videoSize.width}  ,  ${videoSize.height}")
        }
    }

    override fun onPause() {
        super.onPause()
        ADLog.verbose("onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ADLog.verbose("onDestroyView")
    }
}