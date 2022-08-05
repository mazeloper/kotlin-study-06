package com.jschoi.develop.aop_part03_chapter08.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var retrofit: Retrofit
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var fragment: PlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        binding = activityMainBinding

        setContentView(activityMainBinding.root)

        fragment = PlayerFragment()
        retrofit = RetrofitClient.getInstance()

        initAdapter()
        initRecyclerView()
        initFragment()
        getVideoList()
    }

    private fun initAdapter() {
        videoAdapter = VideoAdapter(callback = { url, title ->
            supportFragmentManager.fragments.find { it is PlayerFragment }?.let {
                (it as PlayerFragment).play(url, title)
                ///initFragment()
                fragment.animation()
            }
            binding.fragmentContainer.visibility = View.VISIBLE
        })
    }

    private fun initRecyclerView() {
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