package com.example.geoquest_app.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geoquest_app.model.Repository
import com.example.geoquest_app.model.Reviews
import com.example.models.Treasures
import com.example.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeoViewModel : ViewModel() {
    val repository = Repository()

    // USER VARIABLES
    var userData = MutableLiveData<User>()
    var userImage = MutableLiveData<Bitmap>()
    var userImages = mutableMapOf<Int, Bitmap>()

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
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
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




}