package com.example.geoquest_app.model.retrofit

import com.example.geoquest_app.model.Treasure
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
    suspend fun getAllTreasures(): Response<List<Treasure>>
    @GET("treasure/{id}")
    suspend fun getTreasureById(@Path("id") treasureId: Int): Response<Treasure>
    @GET("treasure/{id}/picture")
    suspend fun getPictureByTreasureId(@Path("id") treasureId: Int): Response<ResponseBody>


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