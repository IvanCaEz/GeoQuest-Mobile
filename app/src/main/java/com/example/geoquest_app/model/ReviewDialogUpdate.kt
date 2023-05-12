package com.example.geoquest_app.model

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.DialogReviewUpdateBinding
import com.example.geoquest_app.viewmodel.GeoViewModel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream


class ReviewDialogUpdate(val review: Reviews): DialogFragment() {
    lateinit var binding: DialogReviewUpdateBinding

    val REQUEST_PERMISSIONS = 1
    var newImageName = ""
    var imagePath = ""
    var imageUri: Uri? = null
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                runBlocking {
                    imageUri = data.data!!
                    binding.reviewPicture.setImageURI(imageUri)
                    // Get the file name from the URI
                    newImageName = getFileName(imageUri!!)
                }
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val viewModel: GeoViewModel by activityViewModels()
            val userID = viewModel.userData.value?.idUser


            val builder = AlertDialog.Builder(it)

            binding = DialogReviewUpdateBinding.inflate(layoutInflater)

            binding.treasureName.text = viewModel.treasureName[review.idTreasure].toString()
            binding.opinionUpdate.editText?.setText(review.opinion)
            binding.ratingUpdate.rating = review.rating.toFloat()
            binding.reviewPicture.setImageBitmap(viewModel.reviewImages[review.idReview])

            binding.selectImage.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request the missing permissions
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
                } else {
                    selectImage(resultLauncher)
                }
            }
            builder.setView(binding.root)
                // Add action buttons
                .setPositiveButton("Update") { dialog, id ->
                    if (imageUri != null){
                        val rating = binding.ratingUpdate.rating.toInt()
                        val opinion = binding.opinionUpdate.editText!!.text.toString()
                        val reviewToUpdate = Reviews(review.idReview, review.idTreasure, review.idUser, opinion, rating, newImageName)
                        try {
                            imagePath = getPathFromUri(requireContext(), imageUri!!)!!
                        } catch (e: java.lang.NullPointerException){
                            Log.e("Error ", e.message, e.cause)
                        }
                        val imageFile = File(imagePath)
                        viewModel.updateReviewByTreasureId(reviewToUpdate, imageFile)
                    } else {
                        val rating = binding.ratingUpdate.rating.toInt()
                        val opinion = binding.opinionUpdate.editText!!.text.toString()
                        val placeholderDrawable = resources.getDrawable(R.drawable.placeholder_review)
                        val file = File(context?.cacheDir, review.photo)
                        val fileOutputStream = FileOutputStream(file)
                        val bitmap = (placeholderDrawable as BitmapDrawable).bitmap
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                        fileOutputStream.close()
                        val filePath = file.absolutePath
                        val imageFile = File(filePath)
                        val reviewToUpdate = Reviews(review.idReview, review.idTreasure, review.idUser, opinion,
                            rating, review.photo)
                        viewModel.updateReviewByTreasureId(reviewToUpdate, imageFile)
                    }
                    Toast.makeText(requireContext(), "Review updated", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
        }


    private fun getFileName(imageUri: Uri): String {
        var result: String? = null
        if (imageUri.scheme == "content") {
            val cursor = context?.contentResolver?.query(imageUri, null, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    result = cursor.getString(column_index)
                }
                cursor.close()
            }
        }
        if (result == null) {
            result = imageUri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result!!
    }

    private fun selectImage(resultLauncher: ActivityResultLauncher<Intent>) {
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
                    selectImage(resultLauncher)
                } else {
                    Toast.makeText(requireContext(),
                        "Please accept the permissions", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

}
