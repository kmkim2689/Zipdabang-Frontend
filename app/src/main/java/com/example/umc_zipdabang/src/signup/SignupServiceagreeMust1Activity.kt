package com.example.umc_zipdabang.src.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_zipdabang.databinding.ActivitySignupServiceagreeMust1Binding

class SignupServiceagreeMust1Activity: AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignupServiceagreeMust1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignupServiceagreeMust1Binding.inflate((layoutInflater))
        setContentView(viewBinding.root)

        viewBinding.signupBackbtn.setOnClickListener {
            val intent = Intent(this, SignupServiceagreeActivity::class.java)
            startActivity(intent)
        }
    }
}