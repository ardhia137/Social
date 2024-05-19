package com.pukimen.social.ui.story

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pukimen.social.data.model.StoryModel
import com.pukimen.social.databinding.ItemRowBinding

class StroyAdapter : ListAdapter<StoryModel, StroyAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
            intent.putExtra(DetailStoryActivity.EXTRA_STORY,story)
            holder.itemView.context.startActivity(intent)
            Log.e(ContentValues.TAG, "onFailurel: ${story}")
        }
    }
    class MyViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryModel){
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}