package com.example.picturepuzzle.ui.leaderboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.picturepuzzle.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LeaderboardFragment : Fragment(R.layout.fragment_leaderboard) {

    private val viewModel: LeaderboardViewModel by viewModels()
    private lateinit var adapter: LeaderboardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LeaderboardAdapter(emptyList())
        val recycler = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_leaderboard)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.ranks.observe(viewLifecycleOwner) { ranks ->
            adapter.updateData(ranks)
        }

        viewModel.loadRank(gridSize = 3) // default 3x3
    }
}