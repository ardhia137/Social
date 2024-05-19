package com.pukimen.social.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pukimen.social.databinding.ActivityMainBinding
import com.pukimen.social.ui.auth.AuthViewModel
import com.pukimen.social.ui.auth.LoginActivity
import com.pukimen.social.ui.story.HomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: AuthViewModel by viewModels {
            factory
        }


        binding.ivSpalsh.alpha = 0f
        binding.ivSpalsh.animate().setDuration(1000).alpha(1f).withEndAction {
            viewModel.getSession().observe(this) { user ->
                if (user.isLogin) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()
                }else{
                    startActivity(Intent(this, LoginActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()
                }
            }
        }
    }
}