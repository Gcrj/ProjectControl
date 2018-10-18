package com.gcrj.projectcontrol.http

import com.gcrj.projectcontrol.BuildConfig
import com.gcrj.projectcontrol.bean.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("checkUpdate")
    fun checkUpdate(@Query("versionCode") versionCode: Int = BuildConfig.VERSION_CODE, @Query("packageName") packageName: String = BuildConfig.APPLICATION_ID, @Query("channel") channel: String = BuildConfig.FLAVOR): Call<ResponseBean<CheckUpdateBean>>

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<ResponseBean<UserBean>>

    @GET("projectList")
    fun projectList(): Call<ResponseBean<List<ProjectBean>>>

    @GET("subProjectByUser")
    fun subProjectListByUser(): Call<ResponseBean<List<SubProjectBean>>>

    @FormUrlEncoded
    @POST("subProjectByUser")
    fun addSubProject(@Field("name") name: String, @Field("projectId") projectId: Int, @Field("deadline") deadline: String): Call<ResponseBean<Nothing>>

    @GET("activity")
    fun activityList(@Query("subProjectId") subProjectId: Int): Call<ResponseBean<List<ActivityBean>>>

    @FormUrlEncoded
    @POST("activity")
    fun addActivity(@Field("subProjectId") subProjectId: Int, @Field("activityName") activityName: String, @Field("activityRelatedName") activityRelatedName: String): Call<ResponseBean<Nothing>>

    @FormUrlEncoded
    @POST("addActivityRelated")
    fun addActivityRelated(@Field("subProjectId") subProjectId: Int, @Field("activityId") activityId: Int, @Field("activityRelatedName") activityRelatedName: String): Call<ResponseBean<Nothing>>

    @FormUrlEncoded
    @POST("updateActivityRelated")
    fun modifyActivityRelated(@Field("subProjectId") subProjectId: Int, @Field("activityRelated") activityRelated: String): Call<ResponseBean<Nothing>>

    @GET("activityRelated")
    fun activityRelatedList(@Query("activityId") activityId: Int): Call<ResponseBean<List<ActivityRelatedBean>>>

    @GET("subProjectByProject")
    fun subProjectListByProject(@Query("projectId") projectId: Int): Call<ResponseBean<List<SubProjectBean>>>

    @POST("sheet")
    fun previewXls(@Body params: RequestBody): Call<ResponseBody>

    @POST("sheetInfo")
    fun submitXls(@Body params: RequestBody): Call<ResponseBean<Nothing>>

    @GET("sheetInfo")
    fun previewXlsProjectList(): Call<ResponseBean<SheetListBean>>

    @PUT("subProjectByUser")
    fun updateSubProject(@Query("id") id: Int?, @Query("name") name: String? = null, @Query("deadline") deadline: String? = null, @Query("completionTime") completionTime: String? = null, @Query("versionName") versionName: String? = null): Call<ResponseBean<Nothing>>

    @DELETE("subProjectByUser")
    fun deleteubProject(@Query("id") id: Int?): Call<ResponseBean<Nothing>>

    @PUT("activity")
    fun updateActivity(@Query("id") id: Int?, @Query("name") name: String? = null): Call<ResponseBean<Nothing>>

    @DELETE("activity")
    fun deleteubActivity(@Query("subProjectId") subProjectId: Int?, @Query("activityId") activityId: Int?): Call<ResponseBean<Nothing>>

    @PUT("updateActivityRelated")
    fun updateActivityRelated(@Query("id") id: Int?, @Query("name") name: String? = null): Call<ResponseBean<Nothing>>

    @DELETE("updateActivityRelated")
    fun deleteubActivityRelated(@Query("subProjectId") subProjectId: Int?, @Query("activityRelatedId") activityRelatedId: Int?): Call<ResponseBean<Nothing>>

    @POST("avator")
    fun modifyAvator(@Body params: RequestBody): Call<ResponseBean<String>>

    @FormUrlEncoded
    @POST("password")
    fun modifyPassword(@Field("password") password: String): Call<ResponseBean<Nothing>>

}