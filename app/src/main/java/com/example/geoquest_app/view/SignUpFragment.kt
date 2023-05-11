package com.example.geoquest_app.view

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
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
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.loading_dialog)
            if (dialog.window != null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()

            val nickname = binding.nickname.editText?.text.toString()
            val emailET = binding.email
            val email = emailET.editText?.text.toString()
            val passET = binding.password
            val password = passET.editText?.text.toString()
            val confPassET = binding.confPassword
            val confPass = confPassET.editText?.text.toString()
            if (nickname.isNotEmpty()) {
                if (viewModel.validatePassword(passET, password) &&
                    viewModel.confirmPassword(confPassET, confPass, password) &&
                    viewModel.validateEmail(emailET, email)) {
                    binding.nickname.error = null
                    binding.nickname.isErrorEnabled = false
                    val encryptedPass = viewModel.encryptPassword(password)

                    val newUser = User(
                        0, nickname, email, encryptedPass,
                        "placeholder_user.png", "Noob", "Player", listOf()
                    )

                    viewModel.postUser(newUser)
                    viewModel.isNewUserCode.observe(viewLifecycleOwner) { code ->
                        when (code) {
                            409 -> {
                                println("no soy nuevo")
                                binding.nickname.isErrorEnabled = true
                                binding.nickname.error = "This username is taken."
                                dialog.dismiss()
                            }
                            201 -> {
                                println("soy nuevo")
                                binding.nickname.error = null
                                binding.nickname.isErrorEnabled = false
                                dialog.dismiss()
                                requireView().findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
                            }
                        }
                    }
                } else dialog.dismiss()
            } else {
                binding.nickname.error = "Nickname can't be empty."
                binding.nickname.isErrorEnabled = true
                dialog.dismiss()
            }
        }
            binding.login.setOnClickListener {
                requireView().findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
            }

        }



}



