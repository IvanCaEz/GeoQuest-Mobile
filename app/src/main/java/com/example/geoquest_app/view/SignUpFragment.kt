package com.example.geoquest_app.view

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.geoquest_app.utils.safeNavigate
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

        registerAnimation()

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
                                findNavController().safeNavigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
                                //getView()?.findNavController()?.navigate(R.id.action_signUpFragment_to_logInFragment)
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
                findNavController().safeNavigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
            }

        }

    private fun registerAnimation(){
        val slideConstraint = binding.registerConstraintSlider
        val mainText = binding.loginTitle
        val firstEditText = binding.nickname
        val emailEditText = binding.email
        val secondEditText = binding.password
        val confPassEditText = binding.confPassword
        val firstTextSignUp = binding.text
        val secondTextSignUp = binding.signup
        val logInButton = binding.login
        val logoImage = binding.logo

        mainText.alpha = 0.0f
        firstEditText.alpha = 0.0f
        emailEditText.alpha = 0.0f
        confPassEditText.alpha = 0.0f
        secondEditText.alpha = 0.0f
        firstTextSignUp.alpha = 0.0f
        secondTextSignUp.alpha = 0.0f
        logInButton.alpha = 0.0f
        logoImage.alpha = 0.0f

        slideConstraint.y = 2500.0f
        slideConstraint.animate().translationY(0.0f).duration = 1500
        Handler(Looper.getMainLooper()).postDelayed({
            mainText.animate().alpha(1.0f).duration = 350
            firstEditText.animate().alpha(1.0f).duration = 350
            secondEditText.animate().alpha(1.0f).duration = 350
            firstTextSignUp.animate().alpha(1.0f).duration = 350
            secondTextSignUp.animate().alpha(1.0f).duration = 350
            logInButton.animate().alpha(1.0f).duration = 350
            logoImage.animate().alpha(1.0f).duration = 350
            emailEditText.animate().alpha(1.0f).duration = 350
            confPassEditText.animate().alpha(1.0f).duration = 350
        }, 1800)
    }



}



