package com.jschoi.develop.aop_part03_chapter08.view

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.video.VideoSize
import com.jschoi.develop.aop_part03_chapter08.ADLog
import com.jschoi.develop.aop_part03_chapter08.R
import com.jschoi.develop.aop_part03_chapter08.adapter.VideoAdapter
import com.jschoi.develop.aop_part03_chapter08.databinding.FragmentPlayerBinding
import com.jschoi.develop.aop_part03_chapter08.dto.VideoDTO
import com.jschoi.develop.aop_part03_chapter08.net.RetrofitClient
import com.jschoi.develop.aop_part03_chapter08.net.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var retrofit: Retrofit
    private var player: ExoPlayer? = null

    override fun onDetach() {
        super.onDetach()
        ADLog.error("####")
    }

    override fun onDestroy() {
        super.onDestroy()
        ADLog.error("####")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ADLog.error("####")
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        retrofit = RetrofitClient.getInstance()
        createSurface()

        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        initControlButton(fragmentPlayerBinding)

        getVideoList()


    }


    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        videoAdapter = VideoAdapter(callback = { url, title ->
            play(url, title)
        })

        fragmentPlayerBinding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private var mSurfaceView: SurfaceView? = null
    private fun createSurface() {
        mSurfaceView = SurfaceView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    if (player != null) {
                        player?.setVideoSurfaceHolder(holder)
                    } else {
                        ADLog.error("#########")
                    }
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    if (mSurfaceView == null) {
                        if (binding.playerView.getChildAt(0) is SurfaceView) {
                            ADLog.error("####")
                        }
                        createSurface()
                    } else if (player != null) {
                        player?.setVideoSurfaceHolder(holder)
                        //mMediaPlayer!!.setDisplay(mSurfaceView!!.holder)
                    }
                    if (player?.isPlaying == true) {
                        player?.pause()
                    }
                    mSurfaceView = null
                }
            })
        }
        binding.playerView.addView(mSurfaceView, 0)
    }

    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding) {

        context?.let {
            player = ExoPlayer.Builder(it).build().apply {
                setVideoSurfaceView(mSurfaceView)
            }
        }
        fragmentPlayerBinding.playerView.player = player

        binding.let {
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if (isPlaying) {
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    super.onVideoSizeChanged(videoSize)
                    ADLog.error("###")
                }
            })
        }
    }

    private fun initControlButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playerView.setOnClickListener {
            // TODO : 2022/08/12
            val state = R.id.fullscreen
            binding.root.transitionToState(state)

            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun getVideoList() {
        retrofit.create(VideoService::class.java).getVideoList()
            .enqueue(object : Callback<VideoDTO> {
                override fun onResponse(call: Call<VideoDTO>, response: Response<VideoDTO>) {
                    if (response.isSuccessful.not()) return
                    response.body()?.let {
                        videoAdapter.submitList(it.videos)
                    }
                }

                override fun onFailure(call: Call<VideoDTO>, t: Throwable) {
                    Log.e("TAG", "ERROR MESSAGE : ${t.message}")
                }
            })
    }

    fun play(url: String, title: String) {
        context?.let {
            // url -> DataSource -> MediaSource -> Player
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player?.setMediaSource(mediaSource)
            player?.prepare()
            player?.play()
        }
        binding?.let {
            // motionLayout end 결과 값으로
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        ADLog.error("###")
        super.onDestroyView()
        player?.release()
    }
}