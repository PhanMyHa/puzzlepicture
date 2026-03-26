package com.example.picturepuzzle.ui.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.picturepuzzle.databinding.ItemRankBinding
import com.example.picturepuzzle.data.firebase.RankEntry

class LeaderboardAdapter(
    private var ranks: List<RankEntry>
) : RecyclerView.Adapter<LeaderboardAdapter.RankViewHolder>() {

    inner class RankViewHolder(
        private val binding: ItemRankBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rank: RankEntry, position: Int) {
            binding.textRankNumber.text = "#${position + 1}"
            binding.textRankUser.text = rank.uid.take(8) + "..."
            binding.textRankScore.text = "Time: ${rank.bestTime}s | Moves: ${rank.bestMoves}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val binding = ItemRankBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        holder.bind(ranks[position], position)
    }

    override fun getItemCount() = ranks.size

    fun updateData(newRanks: List<RankEntry>) {
        ranks = newRanks
        notifyDataSetChanged()
    }
}