package com.example.umc_zipdabang.src.my

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_zipdabang.databinding.ActivityMyWritingBinding

class MyWritingActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityMyWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityMyWritingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.myIngredientPlusbtn.setOnClickListener {

        }

        viewBinding.myBackbtn.setOnClickListener {
            finish()
        }
    }
}