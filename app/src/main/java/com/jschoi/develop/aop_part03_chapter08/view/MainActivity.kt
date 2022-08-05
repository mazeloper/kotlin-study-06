package com.jschoi.develop.aop_part03_chapter08.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jschoi.develop.aop_part03_chapter08.ADLog
import com.jschoi.develop.aop_part03_chapter08.R
import com.jschoi.develop.aop_part03_chapter08.adapter.VideoAdapter
import com.jschoi.develop.aop_part03_chapter08.databinding.ActivityMainBinding
import com.jschoi.develop.aop_part03_chapter08.dto.VideoDTO
import com.jschoi.develop.aop_part03_chapter08.net.RetrofitClient
import com.jschoi.develop.aop_part03_chapter08.net.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Simple Youtube App
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragment: PlayerFragment
    private lateinit var retrofit: Retrofit
    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        binding = activityMainBinding

        setContentView(activityMainBinding.root)
        retrofit = RetrofitClient.getInstance()

        fragment = PlayerFragment()

        initAdapter()
        initRecycler()
        initFragment()

        getVideoList()
    }

    private fun initAdapter() {
        videoAdapter = VideoAdapter(callback = { url, title ->
            fragment.play(url, title)
        })
    }

    private fun initRecycler() {
        binding.mainRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
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
}