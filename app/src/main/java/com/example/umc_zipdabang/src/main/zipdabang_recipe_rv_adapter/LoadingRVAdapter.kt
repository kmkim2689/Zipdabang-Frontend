package com.example.umc_zipdabang.src.main.zipdabang_recipe_rv_adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_zipdabang.databinding.ItemLoadingBinding
import com.example.umc_zipdabang.databinding.ItemRecipesPreviewBinding
import com.example.umc_zipdabang.src.main.MainActivity
import com.example.umc_zipdabang.src.main.ZipdabangRecipeFragment
import com.example.umc_zipdabang.src.main.zipdabang_recipe_data_class.AllRecipesData
import kotlinx.coroutines.NonDisposableHandle.parent

class LoadingRVAdapter(private val context: MainActivity, private var dataList: ArrayList<AllRecipesData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun update(list : ArrayList<AllRecipesData>){
        this.dataList= list
    }

    private var VIEW_TYPE_ITEM = 0
    private var VIEW_TYPE_LOADING = 1

    class ItemViewHolder(private var binding: ItemRecipesPreviewBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, item: AllRecipesData) {

            binding.tvRecipePreview.text= item.beverage
            binding.tvLikes.text=item.likes.toString()
            Glide.with(context)
                .load(item.picUrl)
                .into(binding.ivRecipePreview)
            binding.ivRecipePreview.clipToOutline = true

        }
    }


    class LoadingViewHolder(private var binding: ItemLoadingBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, item: AllRecipesData) {


        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ItemViewHolder) {
            holder.bind(context, dataList[position])
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
        return when (dataList[position].beverage) {
            null -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }


    override fun getItemCount(): Int {
        return dataList.size
    }
}