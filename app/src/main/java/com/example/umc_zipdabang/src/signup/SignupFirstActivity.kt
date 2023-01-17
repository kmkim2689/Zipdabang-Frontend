package com.example.umc_zipdabang.src.signup

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.umc_zipdabang.R
import com.example.umc_zipdabang.databinding.ActivitySignupFirstBinding

class SignupFirstActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignupFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding=ActivitySignupFirstBinding.inflate((layoutInflater))
        setContentView(viewBinding.root)

        var name = viewBinding.signupRealedtName
        var nickname =viewBinding.signupRealedtNickname
        var nextBtn = viewBinding.signupNextbtn

        name.addTextChangedListener{
            if(name.text.toString().length > 0 && nickname.text.toString().length >0){
                    nextBtn.setEnabled(true)
                    nextBtn.setBackgroundResource(R.drawable.sign_btn_round_yellow)
                    nextBtn.setTextColor((ContextCompat.getColor(applicationContext, R.color.jipdabang_black)))
            }
            else{
                nextBtn.setEnabled(false)
                nextBtn.setBackgroundResource(R.drawable.sign_btn_round)
                nextBtn.setTextColor((ContextCompat.getColor(applicationContext, R.color.jipdabang_sign_text_gray)))
            }
        }
        nickname.addTextChangedListener{
            if(name.text.toString().length > 0 && nickname.text.toString().length >0){
                nextBtn.setEnabled(true)
                nextBtn.setBackgroundResource(R.drawable.sign_btn_round_yellow)
                nextBtn.setTextColor((ContextCompat.getColor(applicationContext, R.color.jipdabang_black)))
            }
            else{
                nextBtn.setEnabled(false)
                nextBtn.setBackgroundResource(R.drawable.sign_btn_round)
                nextBtn.setTextColor((ContextCompat.getColor(applicationContext, R.color.jipdabang_sign_text_gray)))
            }
        }

        val sharedPreference = getSharedPreferences("signup",0)
        val editor = sharedPreference.edit()
        //editor.clear()
        //editor.apply()

        var name_sp =sharedPreference.getString("name","")
        var nickname_sp =sharedPreference.getString("nickname","")

        if(name_sp != null && nickname_sp !=null){
            name.setText(sharedPreference.getString("name",""))
            nickname.setText(sharedPreference.getString("nickname",""))
        }

        nextBtn.setOnClickListener{
            if(!validNickname()){
                return@setOnClickListener
            }
            editor.putString("name",name.text.toString())
            editor.putString("nickname",nickname.text.toString())
            editor.apply()
            sharedPreference.getString("name","")?.let { Log.e(TAG, it) }
            sharedPreference.getString("nickname","")?.let { Log.e(TAG, it) }
            /*
            val intent_name = Intent(this, SignupSecondActivity::class.java)
            intent_name.putExtra("name", name.text.toString().trim())
            val intent_nickname = Intent(this, SignupSecondActivity::class.java)
            intent_nickname.putExtra("nickname", nickname.text.toString().trim())
             */
            val intent = Intent(this, SignupSecondActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        viewBinding.signupBackbtn.setOnClickListener{
            val intent = Intent(this,SignupCameraagreeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
    }

    private fun validNickname():Boolean{
        val value: String = viewBinding.signupRealedtNickname?.text.toString().trim()
        val nicknamepattern = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{2,10}$"
        return if(!value.matches(nicknamepattern.toRegex())){
            viewBinding.signupRealedtNickname.error="닉네임 형식이 잘못되었습니다."
            false
        } else{
            viewBinding.signupRealedtNickname.error=null
            true
        }
    }
}