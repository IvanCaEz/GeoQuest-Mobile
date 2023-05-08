package com.example.geoquest_app.retrofit

import com.example.geoquest_app.model.Reports
import com.example.geoquest_app.model.Reviews
import com.example.models.*
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiInterface {

    // Treasure queries
    @GET("treasure")
    suspend fun getAllTreasures(): Response<List<Treasures>>
    @GET("treasure/{id}")
    suspend fun getTreasureById(@Path("id") treasureId: Int): Response<Treasures>
    @GET("treasure/{id}/picture")
    suspend fun getPictureByTreasureId(@Path("id") treasureId: Int): Response<ResponseBody>
    // Treasure Stats queries
    @GET("treasure/{id}/stats")
    suspend fun getTreasureStatsById(@Path("id") treasureId: Int): Response<TreasureStats>
    // Treasure reviews queries
    @GET("treasure/{id}/reviews")
    suspend fun getAllReviewsByTreasureId(@Path("id") treasureId: Int): Response<List<Reviews>>
    @GET("treasure/{treasureId}/reviews/{reviewId}")
    suspend fun getSpecificReviewFromTreasureById(@Path("treasureId") treasureId: Int, @Path("reviewId") idReviews: Int): Response<Reviews>
    @GET("treasure/{treasureID}/reviews/{reviewID}/picture")
    suspend fun getPictureWithSpecificReviewFromTreasureById(@Path("treasureID") treasureId: Int, @Path("reviewID") idReviews: Int): Response<ResponseBody>
    // Treasure reports queries
    @GET("treasure/{id}/reports")
    suspend fun getReportsByTreasureId(@Path("id") treasureId: Int): Response<List<Reports>>
    @GET("treasure/{id}/reports/{reportId}")
    suspend fun getReportByIdFromTreasureId(@Path("id") treasureId: Int, @Path("reportId") idReports: Int): Response<Reports>
    @Multipart
    @POST
    suspend fun postTreasure(@Part("body") body: RequestBody, @Part image: MultipartBody.Part)
    @Multipart
    @PUT("treasure/{id}")
    suspend fun putTreasure(@Part("id") treasureId: Int, @Part("body") body: RequestBody, @Part image: MultipartBody.Part)
    @Multipart
    @POST("/treasure/{treasureId}/reviews")
    suspend fun postReviewByTreasureId(@Path("treasureId") treasureId: Int, @Part("body") body: RequestBody, @Part image: MultipartBody.Part)
    @Multipart
    @PUT("treasure/{treasureId}/reviews/{reviewId}")
    suspend fun putReviewByTreasureId(@Path("treasureId") treasureId: Int, @Path("reviewId") idReviews: Int, @Part("body") body: RequestBody, @Part image: MultipartBody.Part)
    @PUT("treasure/{treasureId}/score")
    suspend fun updateTreasureScore(@Path("treasureId") treasureId: Int, @Body body: Treasures)
    @DELETE("treasure/{id}")
    suspend fun deleteTreasureById(@Path("id") treasureId: Int)
    @DELETE("treasure/{treasureId}/reviews/{reviewId}")
    suspend fun deleteReviewByTreasureId(@Path("treasureId") treasureId: Int, @Path("reviewid") idReviews: Int)
    @DELETE("treasure/{treasureId}/reports/{reportId}")
    suspend fun deleteReportByTreasureId(@Path("treasureId") treasureId: Int, @Path("reportId") idReports: Int)
    @POST("treasure/{id}/games")
    suspend fun postUserGamesByTreasureId(@Path("id") treasureId: Int, @Body body: Games)

    // USER QUERIES
    @GET("user")
    suspend fun getAllUsers(): Response<List<User>>

    // TODO() mirar esto del login para la auth
    @POST("user/login")
    suspend fun postUserForLogin(usernameAndPass: String)
    @GET("user/{id}")
    suspend fun getUserByID(@Path("id") userId: Int): Response<User>
    @GET("user/username/{userName}")
    suspend fun getUserByUserName(@Path("userName") userName: String): Response<User>
    @POST("user")
    suspend fun postUser(@Body body: User)
    @Multipart
    @PUT("user")
    suspend fun putUser(@Part("body") body: RequestBody, @Part image: MultipartBody.Part)
    @DELETE("user/{id}")
    suspend fun deleteUserByID(@Path("id") userId: Int)
    @GET("user/{id}/picture")
    suspend fun getUserPicture(@Path("id") userId: Int): Response<ResponseBody>
    // USER STATS
    @GET("user/{id}/stats")
    suspend fun getUserStats(@Path("id") userId: Int): Response<UserStats>
    // USER FAVS
    @GET("user/{id}/favs")
    suspend fun getUserFavs(@Path("id") userId: Int): Response<List<Treasures>>
    @GET("user/{id}/favs/{treasureID}")
    suspend fun checkIfFav(@Path("id") userId: Int, @Path("treasureID") treasureId: Int): Boolean
    @POST("user/{id}/favs")
    suspend fun postUserFav(@Path("id") userId: Int, @Body body: Int)
    @DELETE("user/{id}/favs/{treasureID}")
    suspend fun deleteUserFav(@Path("id") userId: Int, @Path("treasureID") treasureId: Int)
    // USER REVIEWS
    @GET("user/{id}/reviews")
    suspend fun getUserReviews(@Path("id") userId: Int): Response<List<Reviews>>
    // USER REPORTS
    @GET("user/{id}/reports")
    suspend fun getUserReports(@Path("id") userId: Int): Response<List<Reports>>
    @DELETE("user/{id}/reports/{treasureID}")
    suspend fun deleteUserReport(@Path("id") userId: Int, @Path("treasureID") treasureId: Int)
    @GET("user/{id}/reports/{reportID}")
    suspend fun getUserReportByID(@Path("id") userId: Int, @Path("reportID") reportID: Int): Response<Reports>

    // USER TREASURES
    @GET("user/{id}/treasures")
    suspend fun getUserTreasures(@Path("id") userId: Int): Response<List<Treasures>>

    // REPORT QUERIES
    @GET("reports")
    suspend fun getAllReports(): Response<List<Reports>>
    @POST("reports")
    suspend fun postReport(@Body body: Reports)



    companion object {
        // emulador -> 10.0.2.16
        // itb -> 172.30.5.163
        private const val BASE_URL = "http://172.30.3.105:8080/"
        fun create(): ApiInterface {
            val client = OkHttpClient.Builder().build()
            val gsonClient = GsonBuilder().serializeNulls().setLenient().serializeSpecialFloatingPointValues().create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonClient))
                .client(client)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }

}