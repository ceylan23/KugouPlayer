package com.kugou.player.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kugou.player.R
import com.kugou.player.model.Rank
import com.kugou.player.util.setCoverImage

class RankAdapter(
    private val onItemClick: (Rank) -> Unit
) : ListAdapter<Rank, RankAdapter.RankViewHolder>(RankDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rank, parent, false)
        return RankViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.rank_cover)
        private val tvName: TextView = itemView.findViewById(R.id.rank_name)
        private val tvIntro: TextView = itemView.findViewById(R.id.rank_intro)

        fun bind(rank: Rank) {
            ivCover.setCoverImage(rank.coverUrl)
            tvName.text = rank.name
            tvIntro.text = rank.intro
            itemView.setOnClickListener { onItemClick(rank) }
        }
    }

    private class RankDiffCallback : DiffUtil.ItemCallback<Rank>() {
        override fun areItemsTheSame(oldItem: Rank, newItem: Rank): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Rank, newItem: Rank): Boolean =
            oldItem == newItem
    }
}
