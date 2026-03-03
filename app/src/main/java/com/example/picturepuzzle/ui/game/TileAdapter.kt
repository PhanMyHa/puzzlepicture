package com.example.picturepuzzle.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.picturepuzzle.databinding.ItemTileBinding

class TileAdapter(
    private val onTileClick: (Int) -> Unit
) : ListAdapter<Tile, TileAdapter.TileViewHolder>(TileDiffCallback()) {

    class TileDiffCallback : DiffUtil.ItemCallback<Tile>() {
        override fun areItemsTheSame(oldItem: Tile, newItem: Tile): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tile, newItem: Tile): Boolean {
            return oldItem.currentRotation == newItem.currentRotation
        }

        override fun getChangePayload(oldItem: Tile, newItem: Tile): Any? {
            return if (oldItem.currentRotation != newItem.currentRotation) {
                "rotation"
            } else {
                null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val binding = ItemTileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: TileViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.animateRotation(getItem(position).currentRotation)
        }
    }

    inner class TileViewHolder(
        private val binding: ItemTileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTileClick(position)
                }
            }
        }

        fun bind(tile: Tile) {
            binding.tileImageView.setImageBitmap(tile.bitmap)
            binding.tileImageView.rotation = tile.currentRotation.toFloat()
        }

        fun animateRotation(newRotation: Int) {
            val currentRotation = binding.tileImageView.rotation
            var targetRotation = newRotation.toFloat()

            if (currentRotation == 270f && targetRotation == 0f) {
                targetRotation = 360f
            }

            binding.tileImageView.animate()
                .rotation(targetRotation)
                .setDuration(150)
                .withEndAction {
                    binding.tileImageView.rotation = newRotation.toFloat()
                }
                .start()
        }
    }
}