package com.example.geoquest_app.retrofit

import com.example.geoquest_app.model.Feature
import com.example.geoquest_app.model.RouteResponse
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiInterface {

    companion object {
        private const val BASE_URL = "https://api.openrouteservice.org/"
        fun create(): DirectionsApiInterface {

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(DirectionsApiInterface::class.java)
        }
    }

    @GET("v2/directions/foot-walking")
    suspend fun getRoute(
        @Query("api_key") key: String,
        @Query("start", encoded = true) start: String,
        @Query("end", encoded = true) end: String
    ): Response<RouteResponse>

    @GET("v2/directions/search")
    suspend fun filterLocation(
        @Query("api_key") key: String,
        @Query("text", encoded = true) start: String,
        @Query("focus.point.lon") userLon: String,
        @Query("focus.point.lat") userLat: String,
        @Query("size") results: Int,

    ): Response<Feature>



}