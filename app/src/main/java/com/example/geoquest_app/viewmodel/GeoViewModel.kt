package com.example.geoquest_app.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geoquest_app.model.Repository
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
    var userImages = mutableMapOf<String, Bitmap>()

    // TREASURE VARIABLES
    var treasureListData = MutableLiveData<List<Treasures>>()
    var treasureData = MutableLiveData<Treasures>()


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
    /*
    fun getTreasureByID(treasureID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getUserByID(treasureID)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    userData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }
    fun getAllTreasures() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getUserByUserName()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    userData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }
     */



}