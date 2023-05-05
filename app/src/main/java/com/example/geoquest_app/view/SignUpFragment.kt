package com.example.geoquest_app.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentSignUpBinding
import com.example.geoquest_app.view.MainActivity
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.User
import java.io.File
import java.io.FileOutputStream

class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    private val viewModel: GeoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(false)





        binding.signup.setOnClickListener {
            val nickname = binding.nickname.editText?.text.toString()
            val email = binding.email.editText?.text.toString()
            val password = binding.password.editText?.text.toString()
            val confirmpass = binding.confPassword.editText?.text.toString()
            if (nickname.isNotEmpty()) {
                if (validatePassword(password) && confirmPassword(password)) {


                    val newUser = User(
                        0, nickname, email, password,
                        "placeholder_user.png", "Noob", "Player", listOf()
                    )

                    val placeholderDrawable = resources.getDrawable(R.drawable.usuario)
                    val file = File(context?.cacheDir, "usuario.png")
                    val fileOutputStream = FileOutputStream(file)
                    val bitmap = (placeholderDrawable as BitmapDrawable).bitmap
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                    fileOutputStream.close()
                    val filePath = file.absolutePath
                    val imageFile = File(filePath)

                    viewModel.postUser(newUser)



                    findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
                    /*
                viewModel.getUserByUserName(nickname)
                    viewModel.isNewUser.observe(viewLifecycleOwner) { isNew ->
                        when (isNew) {
                            false -> {
                                binding.nickname.isErrorEnabled = true
                                binding.nickname.error = "This username is taken."
                            }
                            true -> {
                                binding.nickname.error = null
                                binding.nickname.isErrorEnabled = false


                            }
                        }
                    }
                }
            } else {
                binding.nickname.isErrorEnabled = true
                binding.nickname.error = "Username can't be empty."
            }
                 */


                }
            }
        }
            binding.login.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
            }

        }

    private fun validatePassword(password: String): Boolean {
        return if (password.length <= 5) {
            binding.password.isErrorEnabled = true
            binding.password.error = "Password must contain 6 characters or more."
            false
        } else {
            binding.password.error = null
            binding.password.isErrorEnabled = false
            true
        }
    }

    private fun confirmPassword(password: String): Boolean {
        return if (binding.confPassword.editText?.text.toString().trim() != password) {
            binding.confPassword.isErrorEnabled = true
            binding.confPassword.error = "The passwords don't match."
            false
        } else {
            binding.confPassword.error = null
            binding.confPassword.isErrorEnabled = false // Quita el espacio extra
            true
        }
    }

    }



