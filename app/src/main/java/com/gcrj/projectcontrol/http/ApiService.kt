package com.gcrj.projectcontrol.http

import com.gcrj.projectcontrol.bean.LoginBean
import com.gcrj.projectcontrol.bean.ProjectBean
import com.gcrj.projectcontrol.bean.ResponseBean
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<ResponseBean<LoginBean>>

    @GET("projectList")
    fun projectList(): Call<ResponseBean<List<ProjectBean>>>

}