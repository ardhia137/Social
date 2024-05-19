package com.pukimen.social.ui.auth

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pukimen.social.R
import com.pukimen.social.databinding.ActivityRegisterBinding
import com.pukimen.social.ui.ViewModelFactory
import com.pukimen.social.ui.customView.VisibilityToggleButton
import com.pukimen.social.utils.Results
import com.pukimen.social.utils.validation

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: AuthViewModel by viewModels {
            factory
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val name = binding.edRegisterName.text.toString()
            validation("RegisterActivity",email,password,this,name)
            val isValid = validation("RegisterActivity", email, password, this, name)
            if (isValid) {
                viewModel.register(name,email, password).observe(this@RegisterActivity) { result ->
                    if(result != null){
                        when(result){
                            is Results.Loading ->{
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Results.Success<*> -> {
                                binding.progressBar.visibility = View.GONE
                                AlertDialog.Builder(this).apply {
                                    setTitle("Yeah!")
                                    setMessage("Registration Berhasil")
                                    setPositiveButton("Next") { dialog, _ ->
                                        val intent = Intent(context, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        context.startActivity(intent)
                                        dialog.dismiss()
                                    }
                                    create().show()
                                }
                            }
                            is Results.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Terjadi kesalahan" + result.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }



            }
        }

        binding.login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val visibilityToggleButton: VisibilityToggleButton = findViewById(R.id.ed_register_password)
        val visibilityToggleOff: Drawable = ContextCompat.getDrawable(this, R.drawable.ic_visibility_off)!!
        visibilityToggleOff.setBounds(0, 0, visibilityToggleOff.intrinsicWidth, visibilityToggleOff.intrinsicHeight)
        visibilityToggleButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, visibilityToggleOff, null)

        binding.edRegisterPassword.addTextChangedListener(passwordTextWatcher)
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                if (it.length < 8) {
                    binding.edRegisterPassword.error = "Password must be at least 8 characters long"
                } else {
                    binding.edRegisterPassword.error = null
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }



}
