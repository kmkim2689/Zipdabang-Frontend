package com.example.umc_zipdabang.config.src.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.umc_zipdabang.databinding.FragmentOurBinding

class OurFragment: Fragment() {

    private lateinit var  viewBinding: FragmentOurBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentOurBinding.inflate(layoutInflater)
        return viewBinding.root


    }

}