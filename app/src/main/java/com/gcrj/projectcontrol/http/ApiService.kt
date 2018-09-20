package com.gcrj.projectcontrol.http

import com.gcrj.projectcontrol.bean.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<ResponseBean<UserBean>>

    @GET("projectList")
    fun projectList(): Call<ResponseBean<List<ProjectBean>>>

    @GET("subProject")
    fun subProjectList(): Call<ResponseBean<List<SubProjectBean>>>

    @FormUrlEncoded
    @POST("subProject")
    fun addSubProject(@Field("name") name: String, @Field("projectId") projectId: Int): Call<ResponseBean<Nothing>>

    @GET("activity")
    fun activityList(@Query("subProjectId") subProjectId: Int): Call<ResponseBean<List<ActivityBean>>>

    @FormUrlEncoded
    @POST("activity")
    fun addActivity(@Field("subProjectId") subProjectId: Int, @Field("activityName") activityName: String, @Field("activityRelatedName") type: String): Call<ResponseBean<Nothing>>

    @FormUrlEncoded
    @POST("activityRelated")
    fun modifyActivityRelated(@Field("subProjectId") subProjectId: Int, @Field("activityRelated") activityRelated: String): Call<ResponseBean<Nothing>>

}