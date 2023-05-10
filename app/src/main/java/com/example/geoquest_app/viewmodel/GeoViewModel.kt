package com.example.geoquest_app.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geoquest_app.model.Reports
import com.example.geoquest_app.retrofit.Repository
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.model.RouteResponse
import com.example.models.*
import com.example.models.*
import com.google.android.material.textfield.TextInputLayout
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
    var userData = MutableLiveData<User?>()
    var userImage = MutableLiveData<Bitmap>()
    var userImages = mutableMapOf<Int, Bitmap>()
    var userNames = mutableMapOf<Int, String>()
    var isNewUser = MutableLiveData<Boolean>()
    var userFavs = MutableLiveData<List<Treasures>>()
    var isFav = MutableLiveData<Boolean>()
    var userReviews = MutableLiveData<List<Reviews>>()


    // TREASURE VARIABLES
    var treasureListData = MutableLiveData<List<Treasures>>()
    var treasureData = MutableLiveData<Treasures>()
    var treasureImage = MutableLiveData<Bitmap>()
    var treasureImages = mutableMapOf<Int, Bitmap>()
    var treasureName = mutableMapOf<Int, String>()


    // REVIEW VARIABLES
    var reviewListData = MutableLiveData<List<Reviews>>()
    var reviewData = MutableLiveData<Reviews>()


    // USERS
    // USAR ESTA FUNCION PARA CARGAR LOS USERS QUE NO SEAN EL PROPIO USER
    suspend fun getUserByID(userID: Int) {
        val response = repository.getUserByID(userID)
        if (response.isSuccessful) {
            userNames[userID] = response.body()!!.nickName

        } else {
            Log.e("Error " + response.code(), response.message())
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

    suspend fun getUserImage(userID: Int) {
       // CoroutineScope(Dispatchers.IO).launch {
            // Devuelve la portada del libro con la ID indicada
            val response = repository.getUserPicture(userID)
            if (response.isSuccessful) {
           //     withContext(Dispatchers.Main) {
                    val source = response.body()
                    val inputStream = source?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    userImages[userID] = bitmap
             //   }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        //}
    }

    fun postUser(newUser: User) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.postUser(newUser)
        }
    }

    fun putUser(userID: Int, userToUpdate: User, imageFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Gson().toJson(userToUpdate)
            val objectBody = json.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imageRequestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestFile)
            repository.putUser(userID, objectBody, imagePart)
        }
    }

    fun deleteUser(userID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.deleteUserByID(userID)
            userData.postValue(null)
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
                    treasureName[treasureID] = response.body()!!.name
                    treasureData.postValue(response.body())
                }
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    suspend fun getTreasureImage(treasureID: Int) {
        val response = repository.getPictureByTreasureId(treasureID)
        if (response.isSuccessful) {
            val source = response.body()
            val inputStream = source?.byteStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            treasureImages[treasureID] = bitmap
            treasureImage.postValue(bitmap)
        } else {
            Log.e("Error " + response.code(), response.message())
        }
    }

    var treasureStats = MutableLiveData<TreasureStats>()

    fun getTreasureStats(treasureID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getTreasureStatsById(treasureID)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    treasureStats.postValue(response.body())
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


    fun postReview(review: Reviews, imageFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Gson().toJson(review)
            val objectBody = json.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imageRequestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestFile)
            repository.postReviewByTreasureId(review.idTreasure, objectBody, imagePart)
        }
    }

    fun updateTreasureScore(treasureID: Int, treasure: Treasures) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateTreasureScore(treasureID, treasure)
        }
    }

    fun getReviewsByUserId(userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getUserReviews(userId)
            if (response.isSuccessful) {
                userReviews.postValue(response.body())
            } else Log.e("Error " + response.code(), response.message())
        }
    }

    fun deleteReviewByTreasureId(treasureId: Int, reviewId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteReviewByTreasureId(treasureId, reviewId)
        }
    }

    fun updateReviewByTreasureId(reviewToUpdate: Reviews, imageFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Gson().toJson(reviewToUpdate)
            val objectBody = json.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imageRequestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val imagePart =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestFile)
            repository.putReviewByTreasureId(
                reviewToUpdate.idTreasure,
                reviewToUpdate.idReview,
                objectBody,
                imagePart
            )
        }
    }

    // GAMES

    fun postGame(treasureID: Int, game: Games) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.postUserGamesByTreasureId(treasureID, game)
        }
    }

    // FAVS

    fun getUserFavs(userID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getUserFavs(userID)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        userFavs.postValue(response.body())
                        println(response.body())
                    }
                } else {
                    userFavs.postValue(listOf())
                    println("No tiene favs")
                    Log.e("Error " + response.code(), response.message())
                }
            } catch (e: java.lang.IllegalStateException) {
                println("ERROR " + e.message)
            }

        }
    }

    fun addFavTreasure(userID: Int, treasureID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.postUserFav(userID, treasureID)
        }
    }

    fun deleteFavTreasure(userID: Int, treasureID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteUserFav(userID, treasureID)
        }
    }

    fun checkIfTreasureIsFav(userID: Int, treasureID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            isFav.postValue(repository.checkIfFav(userID, treasureID))
        }
    }

    // REPORTS
    fun postReport(report: Reports) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.postReport(report)
        }
    }

    // ROUTES

    val route = MutableLiveData<RouteResponse>()

    suspend fun getRoute(key: String, start: String, end: String) {
        //CoroutineScope(Dispatchers.IO).launch {
        val response = repository.getRoutes(key, start, end)
        if (response.isSuccessful) {
            println("Ruta creada")
            Log.i("ruta", "CREADA")
            route.postValue(response.body())
        } else {
            println("Ruta no creada")
            Log.i("ruta", "MAL")
        }
        //}
    }

    // USER STATS

    fun getUserStats(userId: Int): UserStats? {
        var stats: UserStats? = null
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getUserStats(userId)
            if (response.isSuccessful) {
                stats = response.body()
            } else Log.i("Estadisticas", "MAL")
        }
        return stats
    }


    fun validatePassword(passET: TextInputLayout, password: String): Boolean {
        return if (password.length <= 5) {
            passET.isErrorEnabled = true
            passET.error = "Password must contain 6 characters or more."
            false
        } else {
            passET.error = null
            passET.isErrorEnabled = false
            true
        }
    }

    fun confirmPassword(
        confirmPassET: TextInputLayout,
        confPassword: String,
        password: String
    ): Boolean {
        return if (confPassword.trim() != password) {
            confirmPassET.isErrorEnabled = true
            confirmPassET.error = "The passwords don't match."
            false
        } else {
            confirmPassET.error = null
            confirmPassET.isErrorEnabled = false // Quita el espacio extra
            true
        }
    }

    fun validateEmail(context: Context, email: String): Boolean {
        val emailPattern = Regex(
            "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)\$"
        )
        return if (!emailPattern.matches(email)) {
            Toast.makeText(context, "This is not a valid email.", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }


}