package com.example.geoquest_app.model.retrofit

import com.example.models.Treasures
import com.example.models.User
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    // Treasure queries
    @GET("treasure")
    suspend fun getAllTreasures(): Response<List<Treasures>>
    @GET("treasure/{id}")
    suspend fun getTreasureById(@Path("id") treasureId: Int): Response<Treasures>
    @GET("treasure/{id}/picture")
    suspend fun getPictureByTreasureId(@Path("id") treasureId: Int): Response<ResponseBody>










    // USER Queries

    @GET("user")
    suspend fun getAllUsers(): Response<List<User>>
    @GET("treasure/{id}")
    suspend fun getTreasureByIds(@Path("id") treasureId: Int): Response<Treasures>
    @GET("treasure/{id}/picture")
    suspend fun getPictureByTreasureIds(@Path("id") treasureId: Int): Response<ResponseBody>


    companion object {
        private const val BASE_URL = ""
        fun create(): ApiInterface {
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }

}