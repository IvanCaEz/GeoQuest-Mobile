package com.example.geoquest_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentLogInBinding
import com.example.geoquest_app.viewmodel.GeoViewModel


class LogInFragment : Fragment() {

    lateinit var binding:FragmentLogInBinding
    private val viewModel: GeoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLogInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(false)

        val slideConstraint = binding.loginConstraintSlider
        val mainText = binding.loginTitle
        val firstEditText = binding.nickname
        val secondEditText = binding.password
        val firstTextSignUp = binding.text
        val secondTextSignUp = binding.signup
        val logInButton = binding.login
        val logoImage = binding.logo

        mainText.alpha = 0.0f
        firstEditText.alpha = 0.0f
        secondEditText.alpha = 0.0f
        firstTextSignUp.alpha = 0.0f
        secondTextSignUp.alpha = 0.0f
        logInButton.alpha = 0.0f
        logoImage.alpha = 0.0f

        slideConstraint.y = 2200.0f
        slideConstraint.animate().translationY(0.0f).duration = 1500
        Handler(Looper.getMainLooper()).postDelayed({
            mainText.animate().alpha(1.0f).duration = 350
            firstEditText.animate().alpha(1.0f).duration = 350
            secondEditText.animate().alpha(1.0f).duration = 350
            firstTextSignUp.animate().alpha(1.0f).duration = 350
            secondTextSignUp.animate().alpha(1.0f).duration = 350
            logInButton.animate().alpha(1.0f).duration = 350
            logoImage.animate().alpha(1.0f).duration = 350
        }, 1800)

        binding.login.setOnClickListener {
            val userName = binding.nickname.editText?.text.toString()
            val password = binding.password.editText?.text.toString()
            if (userName.isNotEmpty() && password.isNotEmpty()) {
                viewModel.getUserByUserName(userName)
                viewModel.userData.observe(viewLifecycleOwner) { user ->
                    if (user?.password == password) {
                        binding.password.error = null
                        binding.password.isErrorEnabled = false

                        val toMap = LogInFragmentDirections.actionLogInFragmentToMapFragment()
                        findNavController().navigate(toMap)

                    } else {
                        binding.password.isErrorEnabled = true
                        binding.password.error = "Incorrect password."
                    }
                }
                binding.password.error = null
                binding.password.isErrorEnabled = false
                binding.nickname.error = null
                binding.nickname.isErrorEnabled = false
            } else if (userName.isEmpty() && password.isNotEmpty()) {
                binding.nickname.isErrorEnabled = true
                binding.nickname.error = "Username can't be empty."
                binding.password.error = null
                binding.password.isErrorEnabled = false
            } else if (password.isEmpty() && userName.isNotEmpty()) {
                binding.password.isErrorEnabled = true
                binding.password.error = "Password can't be empty."
                binding.nickname.error = null
                binding.nickname.isErrorEnabled = false
            }
        }

        binding.signup.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }
    }

}