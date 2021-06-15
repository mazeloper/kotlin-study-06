package com.jschoi.develop.aop_part03_chapter08.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jschoi.develop.aop_part03_chapter08.model.VideoModel
import com.jschoi.develop.aop_part03_chapter08.databinding.ItemVideoBinding

class VideoAdapter(val callback: (String, String) -> Unit) :
    ListAdapter<VideoModel, VideoAdapter.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VideoModel>() {
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoModel) {

            binding.titleTextView.text = item.title
            binding.subTitleTextView.text = item.subTitle

            Glide.with(binding.thumbnailImageView.context)
                .load(item.thumb)
                .into(binding.thumbnailImageView)

            binding.root.setOnClickListener {
                callback(item.sources, item.title)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}