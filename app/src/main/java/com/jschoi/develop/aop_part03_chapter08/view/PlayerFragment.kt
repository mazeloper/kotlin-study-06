package com.jschoi.develop.aop_part03_chapter08.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
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
    private var player: SimpleExoPlayer? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding
        initAnim()

        retrofit = RetrofitClient.getInstance()

        // initMotionLayoutEvent(fragmentPlayerBinding)
        initRecyclerView()
        initPlayer()
        initControlButton()

        getVideoList()
    }

    private fun initRecyclerView() = with(binding) {
        videoAdapter = VideoAdapter(callback = { url, title ->
            play(url, title)
        })

        binding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayer() = with(binding) {
        player = SimpleExoPlayer.Builder(requireContext()).build()

        playerView.player = player
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)

                if (isPlaying) {
                    bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_pause_24)
                } else {
                    bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        })
    }

    private fun initControlButton() = with(binding) {
        bottomPlayerControlButton.setOnClickListener {
            val player = player ?: return@setOnClickListener

            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
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
        binding.bottomTitleTextView.text = title
        initAnim()
    }

    fun initAnim() {
        binding.playerMotionLayout.transitionToEnd()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
    }
}