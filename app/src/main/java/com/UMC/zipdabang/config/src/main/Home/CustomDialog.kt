package com.UMC.zipdabang.config.src.main.Home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.UMC.zipdabang.databinding.CustomDialogBinding

class CustomDialogDialog(
    context: AppCompatActivity
) : Dialog(context) { // 뷰를 띄워야하므로 Dialog 클래스는 context를 인자로 받는다.

    private var binding: CustomDialogBinding = CustomDialogBinding.inflate(context.layoutInflater)


    private var onClickListener: ButtonClickListener?= null

    interface ButtonClickListener{
        fun onClicked()
    }

    val dialog= Dialog(context)



    fun setOnClickListener(listener: ButtonClickListener){
        onClickListener = listener
    }

    fun showDialog()
    {

        dialog.setContentView(binding.root)

        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
       dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
       dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setCancelable(true)


       binding.btCancel.setOnClickListener(object  : View.OnClickListener {
           override fun onClick(p0: View?) {
               dialog.dismiss()
           }


       })


        binding.btDelete.setOnClickListener {
            onClickListener?.onClicked()
            dialog.dismiss()
        }

        dialog.show()


    }




    }
