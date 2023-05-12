package com.example.geoquest_app.view
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentLogInBinding
import com.example.geoquest_app.utils.safeNavigate
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.requests.AuthRequest
class LogInFragment : Fragment() {
    lateinit var myPreferences: SharedPreferences
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
        myPreferences = requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

        checkSavedPreferences()

        loginAnimation()

        binding.login.setOnClickListener {
            val userName = binding.nickname.editText?.text.toString()
            val password = binding.password.editText?.text.toString()
            binding.password.error = null
            binding.password.isErrorEnabled = false
            binding.nickname.error = null
            binding.nickname.isErrorEnabled = false
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.loading_dialog)
            if (dialog.window != null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
            if (userName.isNotEmpty() && password.isNotEmpty()) {
                val encryptedPass = viewModel.encryptPassword(password)
                viewModel.getToken(AuthRequest(userName,encryptedPass))
                viewModel.loginAuthCode.observe(viewLifecycleOwner) { code ->
                    when (code) {
                        200 -> {
                            binding.password.error = null
                            binding.password.isErrorEnabled = false
                            if (binding.rememberCheckbox.isChecked) {
                                viewModel.tokenResponseData.observe(viewLifecycleOwner) { tokenResponse ->
                                    myPreferences.edit {
                                        putString("userName", userName)
                                        putString("password", encryptedPass)
                                        putString("token", tokenResponse.token)
                                        putBoolean("active", true)
                                        apply()
                                    }
                                }
                            }
                            login()
                            dialog.dismiss()
                        }
                        404 -> {
                            binding.nickname.isErrorEnabled = true
                            binding.nickname.error = "Username not found"
                            dialog.dismiss()
                        }
                        401 -> {
                            binding.password.isErrorEnabled = true
                            binding.password.error = "Incorrect password"
                            dialog.dismiss()
                        }
                    }
                }
            } else if (userName.isEmpty() && password.isNotEmpty()) {
                binding.nickname.isErrorEnabled = true
                binding.nickname.error = "Username can't be empty."
                binding.password.error = null
                binding.password.isErrorEnabled = false
                dialog.dismiss()
            } else if (password.isEmpty() && userName.isNotEmpty()) {
                binding.password.isErrorEnabled = true
                binding.password.error = "Password can't be empty."
                binding.nickname.error = null
                binding.nickname.isErrorEnabled = false
                dialog.dismiss()
            }  else if (password.isEmpty() && userName.isEmpty()) {
                binding.password.isErrorEnabled = true
                binding.password.error = "Password can't be empty."
                binding.nickname.isErrorEnabled = true
                binding.nickname.error = "Username can't be empty."
                dialog.dismiss()
            }
        }
        binding.signup.setOnClickListener {
            findNavController().safeNavigate( LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
        }
    }

    fun loginAnimation(){
        val slideConstraint = binding.loginConstraintSlider
        val mainText = binding.loginTitle
        val firstEditText = binding.nickname
        val secondEditText = binding.password
        val firstTextSignUp = binding.text
        val secondTextSignUp = binding.signup
        val logInButton = binding.login
        val logoImage = binding.logo
        val checkbox = binding.rememberCheckbox

        mainText.alpha = 0.0f
        firstEditText.alpha = 0.0f
        secondEditText.alpha = 0.0f
        firstTextSignUp.alpha = 0.0f
        secondTextSignUp.alpha = 0.0f
        logInButton.alpha = 0.0f
        logoImage.alpha = 0.0f
        checkbox.alpha = 0.0f

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
            checkbox.animate().alpha(1.0f).duration = 350
        }, 1800)
    }

    private fun checkSavedPreferences(){
        val savedUsername = myPreferences.getString("userName", "")
        val savedPass = myPreferences.getString("password", "")
        val savedToken = myPreferences.getString("token", "")
        val active = myPreferences.getBoolean("active", false)
        if (active){
            viewModel.updateRepository(savedToken!!)
            println("token "+savedToken)
            viewModel.repoIsUpdated.observe(viewLifecycleOwner){
                viewModel.getUserByUserName(savedUsername!!)
                viewModel.userData.observe(viewLifecycleOwner){
                    login()
               }
            }
        }
    }
    private fun login() = findNavController().safeNavigate(LogInFragmentDirections.actionLogInFragmentToMapFragment())
}