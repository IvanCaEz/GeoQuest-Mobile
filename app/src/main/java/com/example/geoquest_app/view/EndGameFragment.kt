
package com.example.geoquest_app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentEndGameBinding
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Games
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class EndGameFragment : Fragment() {

    lateinit var binding: FragmentEndGameBinding
    private val viewModel: GeoViewModel by activityViewModels()
    var imagePath = ""
    var imageUri: Uri? = null
    private val REQUEST_PERMISSIONS = 1
    var newImageName = ""
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                runBlocking {
                    imageUri = data.data!!
                    binding.image.setImageURI(imageUri)
                    // Get the file name from the URI
                    newImageName = getFileName(imageUri!!)

                    println(imageUri.toString())

                    println(newImageName)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEndGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        val treasureID = arguments?.getInt("treasureID")!!
        val startTime = arguments?.getString("startDate")!!
        val endTime = arguments?.getString("endDate")!!

        println("id tesoro:$treasureID")
        println("iid tesoro del viewmodel: ${viewModel.treasureData.value?.idTreasure}")
        val userID = viewModel.userData.value?.idUser

       setUpSpinner()

        val elapsedTime = timeFormatting(startTime, endTime)

        val treasure = viewModel.treasureData.value

        binding.treasureName.text = treasure?.name
        binding.elapsedTime.text = elapsedTime


        binding.image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request the missing permissions
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
            } else {
                selectImage()
            }
        }

        binding.publish.setOnClickListener {
            val rating = binding.rating.rating.toInt()
            val opinion = binding.opinion.editText?.text.toString()
            val result = binding.resultSpinner.selectedItem.toString()

            val solved = result == "FOUND"

            val game = Games(0, treasureID, userID!!, solved,startTime,endTime)

            viewModel.postGame(treasureID, game)

            if (imageUri != null){
                val review = Reviews(0, treasureID,userID,opinion,rating,newImageName)

                try {
                    imagePath = getPathFromUri(requireContext(), imageUri!!)!!
                } catch (e: java.lang.NullPointerException){
                    println("${e.cause} : ${e.message}")
                }
                val imageFile = File(imagePath)

                viewModel.postReview(review, imageFile)
                viewModel.updateTreasureScore(treasureID, treasure!!)

            } else {
                val placeholderDrawable = resources.getDrawable(R.drawable.placeholder_review)
                val file = File(context?.cacheDir, "placeholder_review.png")
                val fileOutputStream = FileOutputStream(file)
                val bitmap = (placeholderDrawable as BitmapDrawable).bitmap
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.close()
                val filePath = file.absolutePath
                val imageFile = File(filePath)
                val review = Reviews(0, treasureID,userID,opinion,rating,"placeholder_review.png")

                viewModel.postReview(review, imageFile)
                viewModel.updateTreasureScore(treasureID, treasure!!)
            }

            findNavController().navigate(R.id.action_endGameFragment_to_listAndSearchFragment)
        }
    }

    fun setUpSpinner(){
        val options = arrayOf("FOUND", "NOT FOUND")
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_found_notfound, options)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_found_notfound)
        binding.resultSpinner.adapter = spinnerAdapter
    }

    fun timeFormatting(startTime: String, endTime: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss")

        val timeDifference =  Duration.between(LocalDateTime.parse(startTime, formatter),
            LocalDateTime.parse(endTime, formatter)).toMillis()

        val hours = TimeUnit.MILLISECONDS.toHours(timeDifference)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference) % 60

        return "$hours:$minutes:$seconds"
    }

    private fun pathFromBitmap(bitmap: Bitmap, imageName: String): String? {
        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),imageName )
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context?.contentResolver?.query(uri,
                null, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    result = cursor.getString(column_index)
                }
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result!!
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split the ID into the type and the actual ID
        val id = wholeID.split(":")[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)

        // Get the cursor for the selected image
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null)

        cursor?.let {
            val columnIndex = cursor.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return filePath
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    selectImage()
                } else {
                    Toast.makeText(requireContext(),
                        "Please accept the permissions", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

}