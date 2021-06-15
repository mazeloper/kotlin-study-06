package com.jschoi.develop.aop_part03_chapter08

import retrofit2.Call
import retrofit2.http.GET

interface VideoService {

    @GET("/v3/4c918e5e-cac3-49bf-9076-d00c678b8333")
    fun getVideoList(): Call<VideoDTO>

}