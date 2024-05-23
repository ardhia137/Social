package com.pukimen.social.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pukimen.social.R
import com.pukimen.social.databinding.ActivityLoginBinding
import com.pukimen.social.ui.ViewModelFactory
import com.pukimen.social.ui.customView.VisibilityToggleButton
import com.pukimen.social.ui.story.HomeActivity
import com.pukimen.social.utils.Results
import com.pukimen.social.utils.validation

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: AuthViewModel by viewModels { factory }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            val isValid = validation("LoginActivity", email, password, this, null)
            if (isValid) {
                viewModel.login(email, password).observe(this@LoginActivity) { result ->
                    if(result != null){
                        when(result){
                            is Results.Loading ->{
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Results.Success<*> -> {
                                binding.progressBar.visibility = View.GONE
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            is Results.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Terjadi kesalahan" + result.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val visibilityToggleButton: VisibilityToggleButton = findViewById(R.id.ed_login_password)
        val visibilityToggleOff = ContextCompat.getDrawable(this, R.drawable.ic_visibility_off)!!
        visibilityToggleOff.setBounds(0, 0, visibilityToggleOff.intrinsicWidth, visibilityToggleOff.intrinsicHeight)
        visibilityToggleButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, visibilityToggleOff, null)
    }
}
