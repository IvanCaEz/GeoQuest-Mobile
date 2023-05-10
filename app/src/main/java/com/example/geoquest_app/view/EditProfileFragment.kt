package com.example.geoquest_app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentEditProfileBinding
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

class EditProfileFragment : Fragment() {
    lateinit var binding: FragmentEditProfileBinding
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
                    binding.userImg.setImageURI(imageUri)
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
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        val user = viewModel.userData.value
        binding.email.editText?.setText(user?.email)


        binding.userImg.setImageBitmap(viewModel.userImages[user?.idUser])

        binding.changeImage.setOnClickListener {
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

        binding.confirmButton.setOnClickListener {
            val newEmail = binding.email.editText?.text.toString()
            val newPass = binding.password.editText?.text.toString()
            var emailUpdated = ""
            var passUpdated = ""

            if (user != null){

                emailUpdated = if (newEmail != user.email) newEmail
                else user.email
                passUpdated = if (newPass != user.password) newPass
                else user.password

                if (imageUri != null){
                    val userToUpdate = User(user.idUser, user.nickName, emailUpdated, passUpdated, newImageName,
                    user.userLevel, user.userRole, listOf())

                    try {
                        imagePath = getPathFromUri(requireContext(), imageUri!!)!!
                    } catch (e: java.lang.NullPointerException){
                        println("${e.cause} : ${e.message}")
                    }
                    val imageFile = File(imagePath)

                    viewModel.putUser(user.idUser, userToUpdate, imageFile)

                } else {
                    val placeholderDrawable = resources.getDrawable(R.drawable.placeholder_review)
                    val file = File(context?.cacheDir, user.photo)
                    val fileOutputStream = FileOutputStream(file)
                    val bitmap = (placeholderDrawable as BitmapDrawable).bitmap
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                    fileOutputStream.close()
                    val filePath = file.absolutePath
                    val imageFile = File(filePath)
                    val userToUpdate = User(user.idUser, user.nickName, emailUpdated, passUpdated, user.photo,
                        user.userLevel, user.userRole, listOf())
                    viewModel.putUser(user.idUser,userToUpdate, imageFile)
                }
            }
            val toProfile = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment()
            findNavController().navigate(toProfile)
        }

        binding.deleteAccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Are you really sure you want to delete your account?")
                .setPositiveButton("Yes, I'm sure") { dialog, which ->
                    viewModel.deleteUser(user!!.idUser)
                    viewModel.userData.observe(viewLifecycleOwner){
                        // TODO limpiar prefs
                        Toast.makeText(requireContext(), "Account deleted succesfully", Toast.LENGTH_SHORT).show()
                        val toLogin = EditProfileFragmentDirections.actionEditProfileFragmentToLogInFragment()
                        findNavController().navigate(toLogin)
                    }
                }
                .setNegativeButton("Take me back"){ dialog, which ->
                    dialog.dismiss()
                }
                .show()
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