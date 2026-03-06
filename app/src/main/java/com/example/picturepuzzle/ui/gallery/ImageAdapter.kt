package com.example.picturepuzzle.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picturepuzzle.R
import com.example.picturepuzzle.data.model.ImageItem
import com.example.picturepuzzle.databinding.ItemImageBinding

class ImageAdapter(
    private val onImageClick: (ImageItem) -> Unit
) : ListAdapter<ImageItem, ImageAdapter.ImageViewHolder>(ImageDiffCallback()) {

    private var completedImageIds: Set<Int> = emptySet()

    fun setCompletedImages(ids: Set<Int>) {
        completedImageIds = ids
        notifyDataSetChanged()
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position), getItem(position).id in completedImageIds)
    }

    inner class ImageViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onImageClick(getItem(position))
                }
            }
        }

        fun bind(image: ImageItem, isCompleted: Boolean) {

            Glide.with(binding.imageView.context)
                .load(image.imageRes)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageView)

            binding.textName.text = image.name
            binding.iconCompleted.isVisible = isCompleted

            binding.imageView.alpha = if (isCompleted) 0.7f else 1f
        }
    }
}