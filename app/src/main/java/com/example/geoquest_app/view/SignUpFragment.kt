package com.example.geoquest_app.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            val passET = binding.password
            val password = passET.editText?.text.toString()
            val confPassET = binding.confPassword
            val confPass = confPassET.editText?.text.toString()
            if (nickname.isNotEmpty()) {
                if (viewModel.validatePassword(passET, password) &&
                    viewModel.confirmPassword(confPassET, confPass, password)) {

                    val newUser = User(
                        0, nickname, email, password,
                        "placeholder_user.png", "Noob", "Player", listOf()
                    )

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





}



