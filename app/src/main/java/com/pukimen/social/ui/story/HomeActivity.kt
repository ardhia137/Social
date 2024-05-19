package com.pukimen.social.ui.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pukimen.social.databinding.ActivityHomeBinding
import com.pukimen.social.ui.auth.ProfileActivity
import com.pukimen.social.ui.ViewModelFactory
import com.pukimen.social.ui.auth.AuthViewModel
import com.pukimen.social.utils.Results

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var token: String? = null
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: StoryViewModel by viewModels {
            factory
        }
        var adapter = StroyAdapter()
        val authViewModel: AuthViewModel by viewModels {
            factory
        }
        authViewModel.getSession().observe(this) { user ->
            token = user.token

            viewModel.getAllStory(token!!).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Results.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Results.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val newsData = result.data
                            adapter.submitList(newsData)
                            binding.listStroy.adapter = adapter
                            Log.e("jancok", "${result.data}")
                        }
                        is Results.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Terjadi kesalahan" + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    binding.listStroy.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                        adapter = adapter
                    }
                }
            }
        }
        val layoutManager = LinearLayoutManager(this)
        binding.listStroy.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.listStroy.addItemDecoration(itemDecoration)

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
        binding.imgItemProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}

