package com.example.geoquest_app.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geoquest_app.retrofit.Repository
import com.example.geoquest_app.model.Reviews
import com.example.models.Games
import com.example.models.Treasures
import com.example.models.User
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class GeoViewModel : ViewModel() {
    val repository = Repository()

    // USER VARIABLES
    var userData = MutableLiveData<User>()
    var userImage = MutableLiveData<Bitmap>()
    var userImages = mutableMapOf<Int, Bitmap>()
    var isNewUser = MutableLiveData<Boolean>()


    // TREASURE VARIABLES
    var treasureListData = MutableLiveData<List<Treasures>>()
    var treasureData = MutableLiveData<Treasures>()
    var treasureImage = MutableLiveData<Bitmap>()
    var treasureImages = mutableMapOf<Int, Bitmap>()


    // REVIEW VARIABLES
    var reviewListData = MutableLiveData<List<Reviews>>()
    var reviewData = MutableLiveData<Reviews>()


    // USERS
    fun getUserByID(userID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getUserByID(userID)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    userData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }
    fun getUserByUserName(userName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getUserByUserName(userName)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    userData.postValue(response.body())
                    isNewUser.postValue(false)

                }
            } else {
                isNewUser.postValue(true)
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun postUser(newUser: User) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.postUser(newUser)
        }
    }

    fun putUser(userToUpdate: User, imageFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Gson().toJson(userToUpdate)
            val objectBody = json.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imageRequestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestFile)
            repository.putUser(objectBody, imagePart)
        }
    }
    fun deleteUser(userID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.deleteUserByID(userID)
        }
    }

    // TREASURES
    fun getAllTreasures() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getAllTreasures()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    treasureListData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }
    fun getTreasureByID(treasureID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getTreasureById(treasureID)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    treasureData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    // REVIEWS
    fun getAllReviews(treasureID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getAllReviewsByTreasureId(treasureID)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    reviewListData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }
    fun getTreasureImage(treasureID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getPictureByTreasureId(treasureID)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    val source = response.body()
                    val inputStream = source?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    treasureImages[treasureID] = bitmap
                    treasureImage.postValue(bitmap)
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    fun postReview(review: Reviews, imageFile: File){
        CoroutineScope(Dispatchers.IO).launch {
            val json = Gson().toJson(review)
            val objectBody = json.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imageRequestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestFile)
            repository.postReviewByTreasureId(review.idTreasure,objectBody, imagePart)
        }
    }

    // GAMES

    fun postGame(treasureID: Int, game: Games){
        CoroutineScope(Dispatchers.IO).launch {
            repository.postUserGamesByTreasureId(treasureID, game)
        }

    }




}