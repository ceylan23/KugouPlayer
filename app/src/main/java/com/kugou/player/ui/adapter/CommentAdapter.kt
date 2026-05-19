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
import com.kugou.player.model.Comment
import com.kugou.player.util.setCoverImage

class CommentAdapter : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.comment_avatar)
        private val tvName: TextView = itemView.findViewById(R.id.comment_user)
        private val tvContent: TextView = itemView.findViewById(R.id.comment_content)
        private val tvLikes: TextView = itemView.findViewById(R.id.comment_likes)
        private val tvTime: TextView = itemView.findViewById(R.id.comment_time)

        fun bind(comment: Comment) {
            ivAvatar.setCoverImage(comment.userAvatar)
            tvName.text = comment.userName
            tvContent.text = comment.content
            tvLikes.text = comment.likeCount.toString()
            tvTime.text = comment.addTime
        }
    }

    private class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.content == newItem.content && oldItem.userName == newItem.userName

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem == newItem
    }
}
