package com.pukimen.social.ui.story

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.pukimen.social.data.model.StoryModel
import com.pukimen.social.data.remote.response.ListStoryItem
import com.pukimen.social.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val story = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY, ListStoryItem::class.java)
        }else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)
        }

        binding.tvDetailName.text = story?.name
        binding.tvDetailDescription.text = story?.description
        Glide.with(binding.root.context)
            .load(story?.photoUrl)
            .into(binding.ivDetailPhoto)

        playAnimation()
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivDetailPhoto, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.tvDetailName, View.ALPHA, 1f).setDuration(6000)
        val description = ObjectAnimator.ofFloat(binding.tvDetailDescription, View.ALPHA, 1f).setDuration(6000)
        val image = ObjectAnimator.ofFloat(binding.ivDetailPhoto, View.ALPHA, 1f).setDuration(6000)


        AnimatorSet().apply {
            playSequentially(name,description, image)
            start()
        }
    }
    companion object{
        const val EXTRA_STORY = "extra_story"
    }
}