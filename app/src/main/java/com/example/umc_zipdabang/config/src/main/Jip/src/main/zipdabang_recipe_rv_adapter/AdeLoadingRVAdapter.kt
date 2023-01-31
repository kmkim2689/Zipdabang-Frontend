package com.example.umc_zipdabang.config.src.main.Jip.src.main.zipdabang_recipe_rv_adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_zipdabang.config.src.main.Jip.src.main.zipdabang_recipe_activities_fragments.RecipeService
import com.example.umc_zipdabang.config.src.main.Jip.src.main.zipdabang_recipe_activities_fragments.ZipdabangRecipeAdeActivity
import com.example.umc_zipdabang.config.src.main.Jip.src.main.zipdabang_recipe_comment.ZipdabangRecipeDetailActivity
import com.example.umc_zipdabang.config.src.main.Jip.src.main.zipdabang_recipe_data_class.AdeRecipesData
import com.example.umc_zipdabang.databinding.ItemLoadingBinding
import com.example.umc_zipdabang.databinding.ItemRecipesPreviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.converter.gson.GsonConverterFactory

class AdeLoadingRVAdapter(private val context: ZipdabangRecipeAdeActivity, private var dataList: ArrayList<AdeRecipesData>, private var idList: ArrayList<Int?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun update(list : ArrayList<AdeRecipesData>){
        this.dataList= list
    }

    private var VIEW_TYPE_ITEM = 0
    private var VIEW_TYPE_LOADING = 1

    class ItemViewHolder(private var binding: ItemRecipesPreviewBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, item: AdeRecipesData, idList: ArrayList<Int?>) {

            binding.tvRecipePreview.text= item.ade
            binding.tvLikes.text=item.likes.toString()
            Glide.with(context)
                .load(item.picUrl)
                .into(binding.ivRecipePreview)
            binding.ivRecipePreview.clipToOutline = true

            itemView.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    val recipeSelectRetrofit = retrofit2.Retrofit.Builder()
                        .baseUrl("http://zipdabang.store:3000")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                    val recipeSelectService = recipeSelectRetrofit.create(RecipeService::class.java)
                    withContext(Dispatchers.Main) {
                        val recipeLocation = adapterPosition
                        val selectedRecipeCommentId = idList[adapterPosition]
                        Log.d("클릭된 레시피의 Id", "${selectedRecipeCommentId}")
                        val sendIntent = Intent(itemView.context, ZipdabangRecipeDetailActivity::class.java)
                        sendIntent.putExtra("recipeId", "${selectedRecipeCommentId?.toString()}")
                        sendIntent.run { itemView.context.startActivity(this)}
                    }
                }
            }

        }
    }


    class LoadingViewHolder(private var binding: ItemLoadingBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, item: AdeRecipesData) {


        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ItemViewHolder) {
            holder.bind(context, dataList[position], idList)
        } else if (holder is LoadingViewHolder) {
            holder.bind(context, dataList[position])
        }
    }


    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val binding =
                ItemRecipesPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemViewHolder(binding)
        } else {
            val binding =
                ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LoadingViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position].ade) {
            null -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }


    override fun getItemCount(): Int {
        return dataList.size
    }
}