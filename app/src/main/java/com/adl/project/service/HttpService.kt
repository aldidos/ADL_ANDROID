package com.adl.project.service

import com.adl.project.model.adl.AdlModel
import com.adl.project.model.test.PostModel
import com.glacier.notihttppost.service.UnsafeOkHttpClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface HttpService {
    @POST("/shop/api")
    @Headers("accept: application/json",
        "content-type: application/x-www-form-urlencoded","charset:utf-8")
    fun post_users(
        @Body jsonparams: PostModel
    ): Call<List<AdlModel>>

    @GET(".")
    @Headers("accept: application/json","charset:utf-8")
    suspend fun getMainData(
        @Query("fromTime") fromTime: String, //요구하는 기본인자를 @Query형태로
        @Query("toTime") toTime: String
    ): String

    @GET(".")
    @Headers("accept: application/json","charset:utf-8")
    suspend fun getEventData(
        @Query("fromTime") fromTime: String,
        @Query("toTime") toTime: String
    ): String

    @GET(".")
    @Headers("accept: application/json","charset:utf-8")
    suspend fun getEnvData(
        @Query("fromTime") fromTime: String,
        @Query("toTime") toTime: String
    ): String

    @GET(".")
    @Headers("accept: application/json","charset:utf-8")
    suspend fun getHistoryData(
        @Query("fromTime") fromTime: String,
        @Query("toTime") toTime: String
    ): String

    @GET(".")
    @Headers("accept: application/json","charset:utf-8")
    suspend fun getMvsData(
        @Query("fromTime") fromTime: String,
        @Query("toTime") toTime: String,
        @Query("fromLocation") fromLocation: String,
        @Query("toLocation") toLocation: String
    ): String

    @GET(".")
    @Headers("accept: application/json","charset:utf-8")
    suspend fun getDeviceData(): String

    @GET(".")
    @Headers("accept: application/json","charset:utf-8")
    suspend fun getLocations(): String

    companion object {
        var okHttpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient

        fun create(BASE_URL: String): HttpService {
            val gson : Gson = GsonBuilder().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(HttpService::class.java)
        }
    }
}