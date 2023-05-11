package com.example.geoquest_app.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geoquest_app.model.Reports
import com.example.geoquest_app.retrofit.Repository
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.model.RouteResponse
import com.example.geoquest_app.model.auth.TokenResponse
import com.example.models.*
import com.example.models.requests.AuthRequest
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
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class GeoViewModel : ViewModel() {
    var repository = Repository("")


    // AUTH VARIABLES
    var tokenResponseData = MutableLiveData<TokenResponse>()
    var repoIsUpdated = MutableLiveData<Boolean>()
    var loginAuthCode = MutableLiveData<Int>()

    // USER VARIABLES
    var userData = MutableLiveData<User?>()
    var userImage = MutableLiveData<Bitmap>()
    var userImages = mutableMapOf<Int, Bitmap>()
    var userNames = mutableMapOf<Int, String>()
    var isNewUser = MutableLiveData<Boolean>()
    var userFavs = MutableLiveData<List<Favourites>>()
    var isFav = MutableLiveData<Boolean>()
    var userReviews = MutableLiveData<List<Reviews>>()
    var isNewUserCode = MutableLiveData<Int>()
    var userStats = MutableLiveData<UserStats>()


    // TREASURE VARIABLES
    var treasureListData = MutableLiveData<List<Treasures>>()
    var treasureData = MutableLiveData<Treasures>()
    var treasureImage = MutableLiveData<Bitmap>()
    var treasureImages = mutableMapOf<Int, Bitmap>()
    var treasureName = mutableMapOf<Int, String>()
    var distanceMapVM = MutableLiveData<Map<Int, Int>>()

    // REVIEW VARIABLES
    var reviewListData = MutableLiveData<List<Reviews>>()
    var reviewData = MutableLiveData<Reviews>()
    var reviewImages = mutableMapOf<Int, Bitmap>()


    // AUTH
    private fun updateRepositoryAndData(tokenResponse: TokenResponse) {
        repository = Repository(tokenResponse.token)
        userData.postValue(tokenResponse.user)
        tokenResponseData.postValue(tokenResponse)
    }

    fun updateRepository(token: String) {
        repository = Repository(token)
        repoIsUpdated.postValue(true)
        println("Repo updated")
    }

    fun getToken(auth: AuthRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postUserForLogin(auth)
            if (response.isSuccessful) {
                println("token vm: ${response.body()!!.token}")
                println("CODE " + response.code())
                println("successfull")
                // Updateamos repositorio con el token
                withContext(Dispatchers.Main) {
                    updateRepositoryAndData(response.body()!!)
                    loginAuthCode.postValue(response.code())
                }
            } else {
                println("no successfull")
                println("CODE " + response.code())
                loginAuthCode.postValue(response.code())
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

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
        val response = repository.getUserPicture(userID)
        if (response.isSuccessful) {
            val source = response.body()
            val inputStream = source?.byteStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            userImage.postValue(bitmap)
            userImages[userID] = bitmap
        } else {
            Log.e("Error " + response.code(), response.message())
        }
    }

    fun postUser(newUser: User) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.postUser(newUser)
            withContext(Dispatchers.Main) {
                isNewUserCode.postValue(response.code())
                println("CODE " + response.code())
            }
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
            } else {
                userReviews.postValue(emptyList())
                Log.e("Error " + response.code(), response.message())
            }
        }
    }

    suspend fun getReviewPicture(treasureID: Int, reviewID: Int) {
        val response = repository.getPictureWithSpecificReviewFromTreasureById(treasureID, reviewID)
        if (response.isSuccessful) {
            val source = response.body()
            val inputStream = source?.byteStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            reviewImages[reviewID] = bitmap
        } else {
            Log.e("Error " + response.code(), response.message())
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
                println("LINEA 315 VieMODEL ERROR " + e.message)
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

    fun getUserReport(userId: Int): Int {
        var size = 0
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getUserReports(userId)
            if (response.isSuccessful){
                if (response.body()!!.isNotEmpty()){
                    size = response.body()!!.size
                } else size = 0
            } else {
                Log.e("Error " + response.code(), response.message())
            }
        }
        return size
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

    fun getUserStats(userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getUserStats(userId)
            if (response.isSuccessful) {
                userStats.postValue(response.body())
            } else Log.i("Estadisticas", "MAL")
        }
    }


    fun validatePassword(passET: TextInputLayout, password: String): Boolean {
        passET.error = null
        val minus = Regex("[a-z]")
        val mayus = Regex("[A-Z]")
        val number = Regex("[0-9]")
        return if (password.length <= 7) {
            passET.isErrorEnabled = true
            passET.error = "Password must contain 8 or more characters"
            false
        } else if (!minus.containsMatchIn(password)){
            passET.isErrorEnabled = true
            passET.error = "Password must contain a lowercase letter."
            false
        } else if (!mayus.containsMatchIn(password)){
            passET.isErrorEnabled = true
            passET.error = "Password must contain an uppercase letter."
            false
        }else if (!number.containsMatchIn(password)){
            passET.isErrorEnabled = true
            passET.error = "Password must contain a number."
            false
        }else{
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

    fun validateEmail(emailET: TextInputLayout, email: String): Boolean {
        val emailPattern = Regex(
            "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)\$"
        )
        return if (!emailPattern.matches(email)) {
            emailET.isErrorEnabled = true
            emailET.error = "Invalid email"
            false
        } else {
            emailET.isErrorEnabled = false
            emailET.error = null
            true
        }
    }

    fun encryptPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hash = messageDigest.digest(password.toByteArray(StandardCharsets.UTF_8))
        val hex = StringBuilder(hash.size * 2)

        for (byte in hash) {
            val hexString = Integer.toHexString(0xff and byte.toInt())
            if (hexString.length == 1) {
                hex.append('0')
            }
            hex.append(hexString)
        }

        return hex.toString()
    }


}