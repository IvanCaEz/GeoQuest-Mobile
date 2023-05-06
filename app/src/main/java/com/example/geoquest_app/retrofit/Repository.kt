package com.example.geoquest_app.retrofit

import com.example.geoquest_app.model.Reports
import com.example.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository {
    val apiInterface = ApiInterface.create()

    // TREASURES
    suspend fun getAllTreasures() = apiInterface.getAllTreasures()

    suspend fun getTreasureById(treasureId: Int)= apiInterface.getTreasureById(treasureId)
    suspend fun getPictureByTreasureId(treasureId: Int) = apiInterface.getPictureByTreasureId(treasureId)
    // TREASURE STATS
    suspend fun getTreasureStatsById(treasureId: Int) = apiInterface.getTreasureStatsById(treasureId)
    // Treasure REVIEWS
    suspend fun getAllReviewsByTreasureId(treasureId: Int) = apiInterface.getAllReviewsByTreasureId(treasureId)
    suspend fun getSpecificReviewFromTreasureById( treasureId: Int, idReviews: Int) = apiInterface.getSpecificReviewFromTreasureById(treasureId, idReviews)
    suspend fun getPictureWithSpecificReviewFromTreasureById( treasureId: Int, idReviews: Int) = apiInterface.getPictureWithSpecificReviewFromTreasureById(treasureId, idReviews)
    // Treasure REPORTS
    suspend fun getReportsByTreasureId(treasureId: Int) = apiInterface.getReportsByTreasureId(treasureId)
    suspend fun getReportByIdFromTreasureId( treasureId: Int, idReports: Int) = apiInterface.getReportByIdFromTreasureId(treasureId, idReports)
    suspend fun postTreasure(body: RequestBody,  image: MultipartBody.Part)  = apiInterface.postTreasure(body, image)
    suspend fun putTreasure(treasureId: Int, body: RequestBody, image: MultipartBody.Part)  = apiInterface.putTreasure(treasureId, body, image)
    suspend fun postReviewByTreasureId(treasureId: Int, body: RequestBody, image: MultipartBody.Part)  = apiInterface.postReviewByTreasureId(treasureId, body, image)
    suspend fun putReviewByTreasureId(treasureId: Int, idReviews: Int, body: RequestBody, image: MultipartBody.Part)  = apiInterface.putReviewByTreasureId(treasureId, idReviews, body, image)
    suspend fun deleteTreasureById(treasureId: Int) = apiInterface.deleteTreasureById(treasureId)
    suspend fun deleteReviewByTreasureId(treasureId: Int, idReviews: Int) = apiInterface.deleteReviewByTreasureId(treasureId, idReviews)
    suspend fun deleteReportByTreasureId(treasureId: Int,  idReports: Int) = apiInterface.deleteReportByTreasureId(treasureId, idReports)
    suspend fun postUserGamesByTreasureId(treasureId: Int, body: Games)  = apiInterface.postUserGamesByTreasureId(treasureId, body)



    // USERS
    suspend fun getAllUsers() = apiInterface.getAllUsers()
    suspend fun postUserForLogin(usernameAndPass: String) = apiInterface.postUserForLogin(usernameAndPass)
    suspend fun getUserByID(userId: Int) = apiInterface.getUserByID(userId)
    suspend fun getUserByUserName(userName: String) = apiInterface.getUserByUserName(userName)
    suspend fun postUser(body:User) = apiInterface.postUser(body)
    suspend fun putUser(body: RequestBody, image: MultipartBody.Part) = apiInterface.putUser(body, image)
    suspend fun deleteUserByID(userId: Int) = apiInterface.deleteUserByID(userId)
    suspend fun getUserPicture(userId: Int) = apiInterface.getUserPicture(userId)
    // USER STATS
    suspend fun getUserStats(userId: Int) = apiInterface.getUserStats(userId)
    // USER FAVS
    suspend fun getUserFavs(userId: Int) = apiInterface.getUserFavs(userId)
    suspend fun postUserFav(userId: Int, body: Int) = apiInterface.postUserFav(userId, body)
    suspend fun deleteUserFav(userId: Int, treasureId: Int) = apiInterface.deleteUserFav(userId, treasureId)
    // USER REVIEWS
    suspend fun getUserReviews(userId: Int) = apiInterface.getUserReviews(userId)
    // USER REPORTS
    suspend fun getUserReports(userId: Int) = apiInterface.getUserReports(userId)
    suspend fun deleteUserReport(userId: Int,  treasureId: Int) = apiInterface.deleteUserReport(userId, treasureId)
    suspend fun getUserReportByID(userId: Int, reportID: Int) = apiInterface.getUserReportByID(userId, reportID)
    // USER TREASURES
    suspend fun getUserTreasures(userId: Int)  = apiInterface.getUserTreasures(userId)
    // REPORT QUERIES
    suspend fun getAllReports() = apiInterface.getAllReports()
    suspend fun postReport(body: Reports) = apiInterface.postReport(body)




}