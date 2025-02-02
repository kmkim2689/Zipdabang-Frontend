package com.UMC.zipdabang.src.my.data

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.UMC.zipdabang.R
import com.UMC.zipdabang.databinding.ItemRecipeEditBinding
import com.bumptech.glide.Glide
import com.UMC.zipdabang.src.my.MyMyrecipeEditActivity
import java.sql.Types.NULL

class MyMyrecipeEditRVAdapter(private val context: MyMyrecipeEditActivity, private var dataList: ArrayList<ItemRecipeChallengeData>)
    : RecyclerView.Adapter<MyMyrecipeEditRVAdapter.ViewHolder>(){

    private var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(v: View?, pos: Int, tag : String )
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    inner class ViewHolder(private var binding: ItemRecipeEditBinding):
        RecyclerView.ViewHolder(binding.root){
        var deletelist : ArrayList<ItemRecipeChallengeData> = arrayListOf()

        fun bind(context: Context, item: ItemRecipeChallengeData, dataList: ArrayList<ItemRecipeChallengeData>){
            binding.myCheckiv.setTag("0")
            binding.myRecipeImg.setColorFilter(NULL)
            binding.myCheckiv.setImageResource(R.drawable.uncheck_round)
            binding.myRecipeTital.text= item.name
            binding.myRecipeHeart.text=item.likes.toString()
            Glide.with(context).load(item.image).into(binding.myRecipeImg)
            binding.myRecipeImg.clipToOutline = true

            binding.myCheckiv.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    val pos=getAdapterPosition()


                    if(binding.myCheckiv.getTag()=="0")
                    {binding.myCheckiv.setImageResource(R.drawable.check_round)
                        binding.myRecipeImg.setColorFilter(Color.parseColor("#80FDEC65"))
                        binding.myCheckiv.setTag("1")}
                    else {
                        binding.myCheckiv.setImageResource(R.drawable.uncheck_round)
                        binding.myRecipeImg.setColorFilter(NULL)
                        binding.myCheckiv.setTag("0")
                    }
                    if (pos != RecyclerView.NO_POSITION) {
                        itemClickListener?.onItemClick(p0, pos, binding.myCheckiv.getTag().toString())


                    }
                }
            })


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= ItemRecipeEditBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, dataList[position],dataList)
    }

    override fun getItemCount(): Int {

        return dataList.size
    }
}
