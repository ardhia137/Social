package com.pukimen.social.ui.story

import android.content.Intent
import android.os.Bundle

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pukimen.social.databinding.ActivityHomeBinding
import com.pukimen.social.ui.MapsActivity
import com.pukimen.social.ui.auth.ProfileActivity
import com.pukimen.social.ui.ViewModelFactory
import com.pukimen.social.utils.LoadingStateAdapter

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: StoryViewModel by viewModels {
            factory
        }
        var adapter = StroyAdapter()

            binding.listStroy.adapter = adapter
            binding.listStroy.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            viewModel.getAllStory.observe(this, {
                adapter.submitData(lifecycle, it)
            })

            binding.iconMap.setOnClickListener {
                startActivity(Intent(this, MapsActivity::class.java))
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

