package com.example.umc_zipdabang.config.src.main.Retrofit

import com.google.gson.Gson
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    private var  BASE_URL = "http://zipdabang.store:3000"

          val retrofit = retrofit2.Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build()

    }


