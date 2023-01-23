package com.example.umc_zipdabang.src.main.zipdabang_recipe_rv_adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_zipdabang.databinding.ItemCommentBinding
import com.example.umc_zipdabang.databinding.ItemLoadingBinding
import com.example.umc_zipdabang.databinding.ItemRecipesPreviewBinding
import com.example.umc_zipdabang.src.main.GlideApp
import com.example.umc_zipdabang.src.main.ZipdabangRecipeSmoothieActivity
import com.example.umc_zipdabang.src.main.zipdabang_recipe_comment.ZipdabangRecipeDetailCommentActivity
import com.example.umc_zipdabang.src.main.zipdabang_recipe_data_class.Comment
import com.example.umc_zipdabang.src.main.zipdabang_recipe_data_class.SmoothieRecipesData
import com.example.umc_zipdabang.src.main.zipdabang_recipe_detail.ZipdabangRecipeDetailSmoothieActivity
import java.time.LocalDate
import java.time.LocalTime

class RecipeDetailCommentRVAdapter(private val commentsList: ArrayList<Comment>): RecyclerView.Adapter<RecipeDetailCommentRVAdapter.CommentsViewHolder>() {
    inner class CommentsViewHolder(private val viewBinding: ItemCommentBinding): RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(comment: Comment) {
            val profilePicUrl = comment.profileImageUrl
            GlideApp.with(itemView)
                .load(profilePicUrl)
                .into(viewBinding.ivCommentProfile)
            viewBinding.tvCommentNickname.text = comment.nickname
            viewBinding.tvCommentDate.text = comment.date.toString()
            viewBinding.tvCommentTime.text = comment.time.toString()
            viewBinding.tvCommentContent.text = comment.content
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val viewBinding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context))
        return CommentsViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(commentsList[position])

    }

    override fun getItemCount(): Int = commentsList.size

}