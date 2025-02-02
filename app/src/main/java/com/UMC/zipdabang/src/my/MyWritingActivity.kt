package com.UMC.zipdabang.src.my

//import com.example.umc_zipdabang.BuildConfig
import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.UMC.zipdabang.R
import com.UMC.zipdabang.databinding.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
//import com.example.umc_zipdabang.BuildConfig
import com.UMC.zipdabang.config.src.main.Home.HomeMainActivity
import com.UMC.zipdabang.config.src.main.Jip.src.main.roomDb.TokenDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MyWritingActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityMyWritingBinding
    private lateinit var binding_upload : DialogUploadBinding
    private lateinit var binding_uploadsuccess : DialogUploadsuccessBinding
    private lateinit var binding_camera : DialogCameraBinding
    private lateinit var binding_save : DialogSaveBinding
    private lateinit var binding_save_warning : DialogSaveWarningBinding
    private lateinit var binding_reallynotsave : DialogReallynotsaveBinding
    private lateinit var binding_notsave : DialogNotsaveBinding
    private lateinit var binding_toast_save : ToastSaveBinding
    private lateinit var binding_toast_delete : ToastDeleteBinding

    private val retrofit = RetrofitInstance.getInstance().create(APIS_My::class.java)
    var token: String = " "

    //임시저장 post 위한 리스트
    var list = arrayOf<String>("","","","","","","","","","","")

    lateinit var fullSizePictureIntents : Intent
    var a : Uri?=null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("URI",a.toString())

        Log.d("카메라 확인 onsave","${a}")
    }

    override fun onPause() {
        super.onPause()
        Log.d("카메라 확인 onpause","onpause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("카메라 확인 onstop","onstop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("카메라 확인 ondestroy","onstop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("카메라 확인 onrestart","onrestart")
    }
    var back=true



    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityMyWritingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        //카메라 허용되어있으면 그냥 넘어가고, 안되어있으면 권한 허용 받기
        if(checkPermission()){

        }else{
            requestPermission()
        }

        Log.d("카메라 확인 oncreate","ㅁ")

        if(savedInstanceState!=null)
        {
            a= Uri.parse(savedInstanceState.getString("URI"))

        }

        val sharedPreference = getSharedPreferences("writing", 0)
        val editor = sharedPreference.edit() //제목, 카테고리, 시간, 한줄소개, 재료이름, 재료갯수, 스텝설명, 후기, 재료스탭 갯수
        val sharedPreference2 = getSharedPreferences("writing_image", 0)
        val editor2 = sharedPreference2.edit() //이미지의 url을 담음 //썸네일, stp1사진, step2사진, ...
        val sharedPreference3 = getSharedPreferences("writing_save",0)
        val editor3= sharedPreference3.edit()
        var recipeId : Int? = 0

        //업로드 버튼 활성화를 위한 변수들
        var title= viewBinding.myRecipeEdtTital
        var time = viewBinding.myRecipeEdtTime
        var describe = viewBinding.myRecipeEdtDescribe
        var aftertip = viewBinding.myRecipeEdtAftertip
        var ingredient1_title = viewBinding.myRecipeEdtIngredientname
        var ingredient1_quan = viewBinding.myRecipeEdtIngredientqun
        var ingredient2_title = viewBinding.myRecipeEdtIngredient2name
        var ingredient2_quan = viewBinding.myRecipeEdtIngredient2qun
        var ingredient3_title = viewBinding.myRecipeEdtIngredient3name
        var ingredient3_quan = viewBinding.myRecipeEdtIngredient3qun
        var ingredient4_title = viewBinding.myRecipeEdtIngredient4name
        var ingredient4_quan = viewBinding.myRecipeEdtIngredient4qun
        var ingredient5_title = viewBinding.myRecipeEdtIngredient5name
        var ingredient5_quan = viewBinding.myRecipeEdtIngredient5qun
        var ingredient6_title = viewBinding.myRecipeEdtIngredient6name
        var ingredient6_quan = viewBinding.myRecipeEdtIngredient6qun
        var ingredient7_title = viewBinding.myRecipeEdtIngredient7name
        var ingredient7_quan = viewBinding.myRecipeEdtIngredient7qun
        var ingredient8_title = viewBinding.myRecipeEdtIngredient8name
        var ingredient8_quan = viewBinding.myRecipeEdtIngredient8qun
        var ingredient9_title = viewBinding.myRecipeEdtIngredient9name
        var ingredient9_quan = viewBinding.myRecipeEdtIngredient9qun
        var ingredient10_title = viewBinding.myRecipeEdtIngredient10name
        var ingredient10_quan = viewBinding.myRecipeEdtIngredient10qun
        var step1_describe = viewBinding.myRecipeEdtStep
        var step2_describe = viewBinding.myRecipeEdtStep2
        var step3_describe = viewBinding.myRecipeEdtStep3
        var step4_describe = viewBinding.myRecipeEdtStep4
        var step5_describe = viewBinding.myRecipeEdtStep5
        var step6_describe = viewBinding.myRecipeEdtStep6
        var step7_describe = viewBinding.myRecipeEdtStep7
        var step8_describe = viewBinding.myRecipeEdtStep8
        var step9_describe = viewBinding.myRecipeEdtStep9
        var step10_describe = viewBinding.myRecipeEdtStep10


        //카테고리 선택 버튼
        viewBinding.myCoffee.setOnClickListener {
            if(viewBinding.myBeverage.isSelected || viewBinding.myTea.isSelected || viewBinding.myAde.isSelected
                || viewBinding.mySmudi.isSelected || viewBinding.myHealth.isSelected){

            }else{
                viewBinding.myCoffee.isSelected = !(viewBinding.myCoffee.isSelected == true)
                if (viewBinding.myCoffee.isSelected) {
                    viewBinding.myCoffee.setBackgroundResource(R.drawable.my_btn_round_notsave_selected)

                } else {
                    viewBinding.myCoffee.setBackgroundResource(R.drawable.my_btn_round_notsave)
                }
            }
        }
        viewBinding.myBeverage.setOnClickListener {
            if(viewBinding.myCoffee.isSelected || viewBinding.myTea.isSelected || viewBinding.myAde.isSelected
                || viewBinding.mySmudi.isSelected || viewBinding.myHealth.isSelected) {

            }else{
                viewBinding.myBeverage.isSelected = !(viewBinding.myBeverage.isSelected == true)
                if (viewBinding.myBeverage.isSelected) {
                    viewBinding.myBeverage.setBackgroundResource(R.drawable.my_btn_round_notsave_selected)

                } else {
                    viewBinding.myBeverage.setBackgroundResource(R.drawable.my_btn_round_notsave)

                }
            }
        }
        viewBinding.myTea.setOnClickListener {
            if(viewBinding.myBeverage.isSelected || viewBinding.myCoffee.isSelected || viewBinding.myAde.isSelected
                || viewBinding.mySmudi.isSelected || viewBinding.myHealth.isSelected){

            }else {
                viewBinding.myTea.isSelected = !(viewBinding.myTea.isSelected == true)
                if (viewBinding.myTea.isSelected) {
                    viewBinding.myTea.setBackgroundResource(R.drawable.my_btn_round_notsave_selected)

                } else {
                    viewBinding.myTea.setBackgroundResource(R.drawable.my_btn_round_notsave)

                }
            }
        }
        viewBinding.myAde.setOnClickListener {
            if(viewBinding.myBeverage.isSelected || viewBinding.myCoffee.isSelected || viewBinding.myTea.isSelected
                || viewBinding.mySmudi.isSelected || viewBinding.myHealth.isSelected){

            }else {
                viewBinding.myAde.isSelected = !(viewBinding.myAde.isSelected == true)
                if (viewBinding.myAde.isSelected) {
                    viewBinding.myAde.setBackgroundResource(R.drawable.my_btn_round_notsave_selected)

                } else {
                    viewBinding.myAde.setBackgroundResource(R.drawable.my_btn_round_notsave)

                }
            }
        }
        viewBinding.mySmudi.setOnClickListener {
            if(viewBinding.myBeverage.isSelected || viewBinding.myCoffee.isSelected || viewBinding.myTea.isSelected
                || viewBinding.myAde.isSelected || viewBinding.myHealth.isSelected){

            }else {
                viewBinding.mySmudi.isSelected = !(viewBinding.mySmudi.isSelected == true)
                if (viewBinding.mySmudi.isSelected) {
                    viewBinding.mySmudi.setBackgroundResource(R.drawable.my_btn_round_notsave_selected)
                } else {
                    viewBinding.mySmudi.setBackgroundResource(R.drawable.my_btn_round_notsave)
                }
            }
        }
        viewBinding.myHealth.setOnClickListener {
            if(viewBinding.myBeverage.isSelected || viewBinding.myCoffee.isSelected || viewBinding.myTea.isSelected
                || viewBinding.myAde.isSelected || viewBinding.mySmudi.isSelected){

            }else {
                viewBinding.myHealth.isSelected = !(viewBinding.myHealth.isSelected == true)
                if (viewBinding.myHealth.isSelected) {
                    viewBinding.myHealth.setBackgroundResource(R.drawable.my_btn_round_notsave_selected)

                } else {
                    viewBinding.myHealth.setBackgroundResource(R.drawable.my_btn_round_notsave)

                }
            }
        }



        //재료 + 버튼 눌렀을때
        var num: Int = 1
        viewBinding.myIngredientPlusbtn.setOnClickListener {
            num++
            if(num> 10){
                num--
            }
            viewBinding.myIngredientNumtv.setText("" + num + "/10")
            if(num == 2){
                viewBinding.myLinearIngredient2User.visibility = View.VISIBLE
            }else if(num==3){
                viewBinding.myLinearIngredient3User.visibility = View.VISIBLE
            }else if(num==4){
                viewBinding.myLinearIngredient4User.visibility = View.VISIBLE
            }else if(num==5){
                viewBinding.myLinearIngredient5User.visibility = View.VISIBLE
            }else if(num==6){
                viewBinding.myLinearIngredient6User.visibility = View.VISIBLE
            }else if(num==7){
                viewBinding.myLinearIngredient7User.visibility = View.VISIBLE
            }else if(num==8){
                viewBinding.myLinearIngredient8User.visibility = View.VISIBLE
            }else if(num==9){
                viewBinding.myLinearIngredient9User.visibility = View.VISIBLE
            }else if(num==10){
                viewBinding.myLinearIngredient10User.visibility = View.VISIBLE
            }
            Log.d("TAG","${num}")
        }
        //재료 - 버튼 눌렀을때
        viewBinding.myIngredientMinusbtn.setOnClickListener{
            if(num==10){
                viewBinding.myRecipeEdtIngredient10name.setText(null)
                viewBinding.myRecipeEdtIngredient10qun.setText(null)
                viewBinding.myLinearIngredient10User.visibility = View.GONE
            }else if(num == 9){
                viewBinding.myRecipeEdtIngredient9name.setText(null)
                viewBinding.myRecipeEdtIngredient9qun.setText(null)
                viewBinding.myLinearIngredient9User.visibility= View.GONE
            }else if(num == 8){
                viewBinding.myRecipeEdtIngredient8name.setText(null)
                viewBinding.myRecipeEdtIngredient8qun.setText(null)
                viewBinding.myLinearIngredient8User.visibility= View.GONE
            }else if(num == 7){
                viewBinding.myRecipeEdtIngredient7name.setText(null)
                viewBinding.myRecipeEdtIngredient7qun.setText(null)
                viewBinding.myLinearIngredient7User.visibility= View.GONE
            }else if(num == 6){
                viewBinding.myRecipeEdtIngredient6name.setText(null)
                viewBinding.myRecipeEdtIngredient6qun.setText(null)
                viewBinding.myLinearIngredient6User.visibility= View.GONE
            }else if(num == 5){
                viewBinding.myRecipeEdtIngredient5name.setText(null)
                viewBinding.myRecipeEdtIngredient5qun.setText(null)
                viewBinding.myLinearIngredient5User.visibility= View.GONE
            }else if(num == 4){
                viewBinding.myRecipeEdtIngredient4name.setText(null)
                viewBinding.myRecipeEdtIngredient4qun.setText(null)
                viewBinding.myLinearIngredient4User.visibility= View.GONE
            }else if(num == 3){
                viewBinding.myRecipeEdtIngredient3name.setText(null)
                viewBinding.myRecipeEdtIngredient3qun.setText(null)
                viewBinding.myLinearIngredient3User.visibility= View.GONE
            }else if(num == 2){
                viewBinding.myRecipeEdtIngredient2name.setText(null)
                viewBinding.myRecipeEdtIngredient2qun.setText(null)
                viewBinding.myLinearIngredient2User.visibility= View.GONE
            }
            num--
            if(num == 0){
                num++
            }
            viewBinding.myIngredientNumtv.setText("" + num + "/10")
            Log.d("TAG","${num}")
        }
        //스텝 +버튼 눌렀을때
        var num2: Int = 1
        viewBinding.myStepPlusbtn.setOnClickListener{
            num2++
            if(num2 > 10){
                num2--
            }
            Log.d("TAG","${num2}")
            viewBinding.myStepNumtv.setText("Step" + num2 + "/Step10")

            if(num2 == 2){
                viewBinding.myStep2.visibility = View.VISIBLE
            }else if(num2==3){
                viewBinding.myStep3.visibility = View.VISIBLE
            }else if(num2==4){
                viewBinding.myStep4.visibility = View.VISIBLE
            }else if(num2==5){
                viewBinding.myStep5.visibility = View.VISIBLE
            }else if(num2==6){
                viewBinding.myStep6.visibility = View.VISIBLE
            }else if(num2==7){
                viewBinding.myStep7.visibility = View.VISIBLE
            }else if(num2==8){
                viewBinding.myStep8.visibility = View.VISIBLE
            }else if(num2==9){
                viewBinding.myStep9.visibility = View.VISIBLE
            }else if(num2==10){
                viewBinding.myStep10.visibility = View.VISIBLE
            }
        }
        //스텝 -버튼 눌렀을때
        viewBinding.myStepMinusbtn.setOnClickListener{
            if(num2==10){
                viewBinding.myRecipeRealimageXbtn10.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep10.setImageResource(0)
                viewBinding.myRecipeEdtStep10.setText(null)
                viewBinding.myStep10.visibility = View.GONE
                list[10] =""
            }else if(num2 == 9){
                viewBinding.myRecipeRealimageXbtn9.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep9.setImageResource(0)
                viewBinding.myRecipeEdtStep9.setText(null)
                viewBinding.myStep9.visibility= View.GONE
                list[9] =""
            }else if(num2 == 8){
                viewBinding.myRecipeRealimageXbtn8.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep8.setImageResource(0)
                viewBinding.myRecipeEdtStep8.setText(null)
                viewBinding.myStep8.visibility= View.GONE
                list[8] =""
            }else if(num2 == 7){
                viewBinding.myRecipeRealimageXbtn7.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep7.setImageResource(0)
                viewBinding.myRecipeEdtStep7.setText(null)
                viewBinding.myStep7.visibility= View.GONE
                list[7] =""
            }else if(num2 == 6){
                viewBinding.myRecipeRealimageXbtn6.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep6.setImageResource(0)
                viewBinding.myRecipeEdtStep6.setText(null)
                viewBinding.myStep6.visibility= View.GONE
                list[6] =""
            }else if(num2 == 5){
                viewBinding.myRecipeRealimageXbtn5.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep5.setImageResource(0)
                viewBinding.myRecipeEdtStep5.setText(null)
                viewBinding.myStep5.visibility= View.GONE
                list[5] =""
            }else if(num2 == 4){
                viewBinding.myRecipeRealimageXbtn4.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep4.setImageResource(0)
                viewBinding.myRecipeEdtStep4.setText(null)
                viewBinding.myStep4.visibility= View.GONE
                list[4] =""
            }else if(num2 == 3){
                viewBinding.myRecipeRealimageXbtn3.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep3.setImageResource(0)
                viewBinding.myRecipeEdtStep3.setText(null)
                viewBinding.myStep3.visibility= View.GONE
                list[3] =""
            }else if(num2 == 2){
                viewBinding.myRecipeRealimageXbtn2.visibility = View.INVISIBLE
                viewBinding.myRecipeRealimageStep2.setImageResource(0)
                viewBinding.myRecipeEdtStep2.setText(null)
                viewBinding.myStep2.visibility= View.GONE
                list[2] =""
            }
            num2--
            if(num2 == 0){
                num2++
            }
            viewBinding.myStepNumtv.setText("Step" + num2 + "/Step10")
            Log.d("TAG","${num2}")
        }


        //뒤로가기 버튼 눌렀을때
        viewBinding.myBackbtn.setOnClickListener {
            var category_check = false
            Log.d("backk","/")
            if(viewBinding.myCoffee.isSelected ==false
                && viewBinding.myAde.isSelected  ==false
                && viewBinding.myTea.isSelected ==false
                && viewBinding.myBeverage.isSelected  ==false
                && viewBinding.mySmudi.isSelected ==false
                && viewBinding.myHealth.isSelected ==false){
                category_check = false
            }else{
                category_check = true
            }

            if(title.text.toString().length ==0 && time.text.toString().length ==0
                && describe.text.toString().length ==0 && aftertip.text.toString().length ==0
                && ingredient1_quan.text.toString().length ==0 && ingredient1_title.text.toString().length ==0
                && ingredient2_quan.text.toString().length ==0 && ingredient2_title.text.toString().length ==0
                && ingredient3_quan.text.toString().length ==0 && ingredient3_title.text.toString().length ==0
                && ingredient4_quan.text.toString().length ==0 && ingredient4_title.text.toString().length ==0
                && ingredient5_quan.text.toString().length ==0 && ingredient5_title.text.toString().length ==0
                && ingredient6_quan.text.toString().length ==0 && ingredient6_title.text.toString().length ==0
                && ingredient7_quan.text.toString().length ==0 && ingredient7_title.text.toString().length ==0
                && ingredient8_quan.text.toString().length ==0 && ingredient8_title.text.toString().length ==0
                && ingredient9_quan.text.toString().length ==0 && ingredient9_title.text.toString().length ==0
                && ingredient10_quan.text.toString().length ==0 && ingredient10_title.text.toString().length ==0
                && step1_describe.text.toString().length ==0 && list[1]==""
                && step2_describe.text.toString().length ==0 && list[2]==""
                && step3_describe.text.toString().length ==0 && list[3]==""
                && step4_describe.text.toString().length ==0 && list[4]==""
                && step5_describe.text.toString().length ==0 && list[5]==""
                && step6_describe.text.toString().length ==0 && list[6]==""
                && step7_describe.text.toString().length ==0 && list[7]==""
                && step8_describe.text.toString().length ==0 && list[8]==""
                && step9_describe.text.toString().length ==0 && list[9]==""
                && step10_describe.text.toString().length ==0 && list[10]==""
                && list[0] =="" && category_check == false){

               val intent = Intent(this,HomeMainActivity::class.java)
                startActivity(intent)
            }
            else{
                binding_reallynotsave = DialogReallynotsaveBinding.inflate(layoutInflater)
                val dialog_reallynotsave_builder = AlertDialog.Builder(this).setView(binding_reallynotsave.root)
                val dialog_reallynotsave = dialog_reallynotsave_builder.create()

                dialog_reallynotsave.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                dialog_reallynotsave.setCanceledOnTouchOutside(true)
                dialog_reallynotsave.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog_reallynotsave.window?.setGravity(Gravity.BOTTOM)
                dialog_reallynotsave.setCancelable(true)

                //레시피 쓰던거 삭제하겠다
                binding_reallynotsave.myDeletebtn.setOnClickListener{


                    binding_notsave = DialogNotsaveBinding.inflate(layoutInflater)
                    val dialog_notsave_builder = AlertDialog.Builder(this).setView(binding_notsave.root)
                    val dialog_notsave = dialog_notsave_builder.create()

                    dialog_notsave.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                    dialog_notsave.setCanceledOnTouchOutside(true)
                    dialog_notsave.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog_notsave.window?.setGravity(Gravity.BOTTOM)
                    dialog_notsave.setCancelable(true)

                    //취소 버튼 눌렀을때
                    binding_notsave.myCancelbtn.setOnClickListener{

                        val intent = Intent(this,HomeMainActivity::class.java)
                        startActivity(intent)
                        dialog_notsave.onBackPressed()
                        dialog_reallynotsave.dismiss()

                    }
                    //삭제하기 버튼 눌렀을때
                    binding_notsave.myDeletebtn.setOnClickListener{
                        CustomToast.createToast(applicationContext, "작성 중인 레시피를 삭제하였어요")?.show()

                        val intent = Intent(this,HomeMainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog_notsave.show()
                }
                //레시피 쓰던거 삭제안할거임
                binding_reallynotsave.myCancelbtn.setOnClickListener {

                    val intent = Intent(this,HomeMainActivity::class.java)
                    startActivity(intent)
                    dialog_reallynotsave.onBackPressed()
                }

                dialog_reallynotsave.show()
            }
        }

        //임시저장 버튼 눌렀을때
        viewBinding.mySavebtn.setOnClickListener {

            editor.putInt("ingredient",num)
            editor.putInt("step",num2)
            editor.putString("title", viewBinding.myRecipeEdtTital.text.toString())
            editor.putString("time", viewBinding.myRecipeEdtTime.text.toString())
            editor.putString("describe", viewBinding.myRecipeEdtDescribe.text.toString())
            editor.putString("aftertip", viewBinding.myRecipeEdtAftertip.text.toString())
            editor.putString("ingredient1_title",viewBinding.myRecipeEdtIngredientname.text.toString())
            editor.putString("ingredient1_quan",viewBinding.myRecipeEdtIngredientqun.text.toString())
            editor.putString("ingredient2_title",viewBinding.myRecipeEdtIngredient2name.text.toString())
            editor.putString("ingredient2_quan",viewBinding.myRecipeEdtIngredient2qun.text.toString())
            editor.putString("ingredient3_title",viewBinding.myRecipeEdtIngredient3name.text.toString())
            editor.putString("ingredient3_quan",viewBinding.myRecipeEdtIngredient3qun.text.toString())
            editor.putString("ingredient4_title",viewBinding.myRecipeEdtIngredient4name.text.toString())
            editor.putString("ingredient4_quan",viewBinding.myRecipeEdtIngredient4qun.text.toString())
            editor.putString("ingredient5_title",viewBinding.myRecipeEdtIngredient5name.text.toString())
            editor.putString("ingredient5_quan",viewBinding.myRecipeEdtIngredient5qun.text.toString())
            editor.putString("ingredient6_title",viewBinding.myRecipeEdtIngredient6name.text.toString())
            editor.putString("ingredient6_quan",viewBinding.myRecipeEdtIngredient6qun.text.toString())
            editor.putString("ingredient7_title",viewBinding.myRecipeEdtIngredient7name.text.toString())
            editor.putString("ingredient7_quan",viewBinding.myRecipeEdtIngredient7qun.text.toString())
            editor.putString("ingredient8_title",viewBinding.myRecipeEdtIngredient8name.text.toString())
            editor.putString("ingredient8_quan",viewBinding.myRecipeEdtIngredient8qun.text.toString())
            editor.putString("ingredient9_title",viewBinding.myRecipeEdtIngredient9name.text.toString())
            editor.putString("ingredient9_quan",viewBinding.myRecipeEdtIngredient9qun.text.toString())
            editor.putString("ingredient10_title",viewBinding.myRecipeEdtIngredient10name.text.toString())
            editor.putString("ingredient10_quan",viewBinding.myRecipeEdtIngredient10qun.text.toString())
            editor.putString("step1_describe",viewBinding.myRecipeEdtStep.text.toString())
            editor.putString("step2_describe",viewBinding.myRecipeEdtStep2.text.toString())
            editor.putString("step3_describe",viewBinding.myRecipeEdtStep3.text.toString())
            editor.putString("step4_describe",viewBinding.myRecipeEdtStep4.text.toString())
            editor.putString("step5_describe",viewBinding.myRecipeEdtStep5.text.toString())
            editor.putString("step6_describe",viewBinding.myRecipeEdtStep6.text.toString())
            editor.putString("step7_describe",viewBinding.myRecipeEdtStep7.text.toString())
            editor.putString("step8_describe",viewBinding.myRecipeEdtStep8.text.toString())
            editor.putString("step9_describe",viewBinding.myRecipeEdtStep9.text.toString())
            editor.putString("step10_describe",viewBinding.myRecipeEdtStep10.text.toString())
            editor.apply()


            //카테고리 선택
            if (viewBinding.myCoffee.isSelected) {
                editor.putString("category", "coffee")
                editor.apply()
            } else if (viewBinding.myBeverage.isSelected) {
                editor.putString("category", "beverage")
                editor.apply()
            } else if (viewBinding.myTea.isSelected) {
                editor.putString("category", "tea")
                editor.apply()
            } else if (viewBinding.myAde.isSelected) {
                editor.putString("category", "ade")
                editor.apply()
            } else if (viewBinding.mySmudi.isSelected) {
                editor.putString("category", "smudi")
                editor.apply()
            } else if (viewBinding.myHealth.isSelected) {
                editor.putString("category", "health")
                editor.apply()
            } else {
                editor.putString("category", "")
                editor.apply()
            }

            /*sharedPreference.getString("category", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("time", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("aftertip", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient1_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient1_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient2_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient2_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient3_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient3_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient4_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient4_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient5_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient5_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient6_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient6_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient7_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient7_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient8_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient8_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient9_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient9_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient10_title", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("ingredient10_quan", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step1_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step2_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step3_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step4_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step5_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step6_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step7_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step8_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step9_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference.getString("step10_describe", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step1_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step2_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step3_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step4_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step5_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step6_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step7_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step8_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step9_image", "@")?.let { Log.e(ContentValues.TAG, it) }
            sharedPreference2.getString("step10_image", "@")?.let { Log.e(ContentValues.TAG, it) }*/


                //임시저장 주의 dialog 띄우기
                binding_save_warning = DialogSaveWarningBinding.inflate(layoutInflater)
                val dialog_save_warning_builder = AlertDialog.Builder(this).setView(binding_save_warning.root)
                val dialog_save_warning = dialog_save_warning_builder.create()

                dialog_save_warning.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                dialog_save_warning.setCanceledOnTouchOutside(true)
                dialog_save_warning.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog_save_warning.window?.setGravity(Gravity.BOTTOM)
                dialog_save_warning.setCancelable(true)

                binding_save_warning.myCancelbtn.setOnClickListener{
                    editor3.putString("filled","0")
                    editor3.apply()
                    editor.clear()
                    editor.apply()
                    dialog_save_warning.onBackPressed()
                }
                binding_save_warning.mySavebtn.setOnClickListener{
                    editor3.putString("filled","1")
                    editor3.apply()

                    //api호출하기기
                    Log.d("통신 리스트", list[0])
                    Log.d("통신 리스트", list[1])
                    Log.d("통신 리스트", list[2])
                    Log.d("통신 리스트", list[3])
                    Log.d("통신 리스트", list[4])
                    Log.d("통신 리스트", list[5])
                    Log.d("통신 리스트", list[6])
                    Log.d("통신 리스트", list[7])
                    Log.d("통신 리스트", list[8])
                    Log.d("통신 리스트", list[9])
                    Log.d("통신 리스트", list[10])

                    for(i in 0..num2){
                        if(list[i] =="")
                            list[i] = "null"
                    }

                    var body2 = arrayListOf<PostNewRecipeStepsImage>(
                        PostNewRecipeStepsImage("","",list[1]),
                        PostNewRecipeStepsImage("","",list[2]),
                        PostNewRecipeStepsImage("","",list[3]),
                        PostNewRecipeStepsImage("","",list[4]),
                        PostNewRecipeStepsImage("","",list[5]),
                        PostNewRecipeStepsImage("","",list[6]),
                        PostNewRecipeStepsImage("","",list[7]),
                        PostNewRecipeStepsImage("","",list[8]),
                        PostNewRecipeStepsImage("","",list[9]),
                        PostNewRecipeStepsImage(
                            "", "", list[10],
                        )
                    )

                    //var step_num = num2
                    var body = PostNewRecipeSaveImage(
                        list[0],
                        body2,
                        num2
                    )

                    GlobalScope.launch(Dispatchers.IO) {
                        val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                        token = tokenDb.tokenDao().getToken().token.toString()
                         retrofit.post_newrecipe_saveimage(token, body).enqueue(object: Callback<PostNewRecipeBodyResponse>{
                            override fun onResponse(
                                call: Call<PostNewRecipeBodyResponse>,
                                response: Response<PostNewRecipeBodyResponse>
                            ) {
                                Log.d("통신", "통신은 성공임")
                                var result = response.body()
                                var isSuccess = result?.success
                                Log.d("통신", isSuccess.toString())
                            }

                            override fun onFailure(call: Call<PostNewRecipeBodyResponse>, t: Throwable) {
                                t.message?.let { it1 -> Log.d("통신", it1) }
                            }

                        })
                    }



                    dialog_save_warning.dismiss()
                    CustomToast.createToast(applicationContext, "작성 중인 레시피를 임시저장하였어요")?.show()
                    finish()
                }
                dialog_save_warning.show()



        }

        //업로드 버튼 눌렀을때
        viewBinding.myUploadbtn.setOnClickListener {
            //카테고리 체크를 위한 변수
            var category_check = false //?
            if(viewBinding.myCoffee.isSelected ==false
                && viewBinding.myAde.isSelected  ==false
                && viewBinding.myTea.isSelected ==false
                && viewBinding.myBeverage.isSelected  ==false
                && viewBinding.mySmudi.isSelected ==false
                && viewBinding.myHealth.isSelected ==false){
                category_check = false
            }else{
                category_check = true
            }

            var check = "nonempty"
            if(title.text.toString().length >0 && time.text.toString().length >0
                && describe.text.toString().length >0 && aftertip.text.toString().length >0
                && ingredient1_quan.text.toString().length >0 && ingredient1_title.text.toString().length >0
                && step1_describe.text.toString().length >0 && list[1]!=""
                && list[0] !="" && category_check == true){

                if(ingredient2_title.text.toString().length ==0)
                    if(num >= 2){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient2_quan.text.toString().length ==0)
                    if(num >= 2){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient3_title.text.toString().length ==0)
                    if(num >= 3){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient3_quan.text.toString().length ==0)
                    if(num >= 3){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient4_title.text.toString().length ==0)
                    if(num >= 4){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient4_quan.text.toString().length ==0)
                    if(num >= 4){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient5_title.text.toString().length ==0)
                    if(num >= 5){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient5_quan.text.toString().length ==0)
                    if(num >= 5){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient6_title.text.toString().length ==0)
                    if(num >= 6){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient6_quan.text.toString().length ==0)
                    if(num >= 6){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient7_title.text.toString().length ==0)
                    if(num >= 7){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient7_quan.text.toString().length ==0)
                    if(num >= 7){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient8_title.text.toString().length ==0)
                    if(num >= 8){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient8_quan.text.toString().length ==0)
                    if(num >= 8){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient9_title.text.toString().length ==0)
                    if(num >= 9){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient9_quan.text.toString().length ==0)
                    if(num >= 9){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient10_title.text.toString().length ==0)
                    if(num >= 10){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(ingredient10_quan.text.toString().length ==0)
                    if(num >= 10){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step1_describe.text.toString().length ==0)
                    if(num2 >= 1){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step2_describe.text.toString().length ==0)
                    if(num2 >= 2){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step3_describe.text.toString().length ==0)
                    if(num2 >= 3){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step4_describe.text.toString().length ==0)
                    if(num2 >= 4){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step5_describe.text.toString().length ==0)
                    if(num2 >= 5){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step6_describe.text.toString().length ==0)
                    if(num2 >= 6){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step7_describe.text.toString().length ==0)
                    if(num2 >= 7){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step8_describe.text.toString().length ==0)
                    if(num2 >= 8){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step9_describe.text.toString().length ==0)
                    if(num2 >= 9){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(step10_describe.text.toString().length ==0)
                    if(num2 >= 10){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[2]=="")
                    if(num2 >= 2){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[3]=="")
                    if(num2 >= 3){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[4]=="")
                    if(num2 >= 4){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[5]=="")
                    if(num2 >= 5){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[6]=="")
                    if(num2 >= 6){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[7]=="")
                    if(num2 >= 7){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[8]=="")
                    if(num2 >= 8){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[9]=="")
                    if(num2 >= 9){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }
                if(list[10]=="")
                    if(num2 >= 10){
                        check = "empty"
                        CustomToast.createToast(applicationContext, "    모든 항목을 채워주세요")?.show()
                    }

                //다 채웠을때 업로드 활성화!
                if(check=="nonempty"){
                    editor.putInt("ingredient",num)
                    editor.putInt("step",num2)
                    editor.putString("title", viewBinding.myRecipeEdtTital.text.toString())
                    editor.putString("time", viewBinding.myRecipeEdtTime.text.toString())
                    editor.putString("describe", viewBinding.myRecipeEdtDescribe.text.toString())
                    editor.putString("aftertip", viewBinding.myRecipeEdtAftertip.text.toString())
                    editor.putString("ingredient1_title",viewBinding.myRecipeEdtIngredientname.text.toString())
                    editor.putString("ingredient1_quan",viewBinding.myRecipeEdtIngredientqun.text.toString())
                    editor.putString("ingredient2_title",viewBinding.myRecipeEdtIngredient2name.text.toString())
                    editor.putString("ingredient2_quan",viewBinding.myRecipeEdtIngredient2qun.text.toString())
                    editor.putString("ingredient3_title",viewBinding.myRecipeEdtIngredient3name.text.toString())
                    editor.putString("ingredient3_quan",viewBinding.myRecipeEdtIngredient3qun.text.toString())
                    editor.putString("ingredient4_title",viewBinding.myRecipeEdtIngredient4name.text.toString())
                    editor.putString("ingredient4_quan",viewBinding.myRecipeEdtIngredient4qun.text.toString())
                    editor.putString("ingredient5_title",viewBinding.myRecipeEdtIngredient5name.text.toString())
                    editor.putString("ingredient5_quan",viewBinding.myRecipeEdtIngredient5qun.text.toString())
                    editor.putString("ingredient6_title",viewBinding.myRecipeEdtIngredient6name.text.toString())
                    editor.putString("ingredient6_quan",viewBinding.myRecipeEdtIngredient6qun.text.toString())
                    editor.putString("ingredient7_title",viewBinding.myRecipeEdtIngredient7name.text.toString())
                    editor.putString("ingredient7_quan",viewBinding.myRecipeEdtIngredient7qun.text.toString())
                    editor.putString("ingredient8_title",viewBinding.myRecipeEdtIngredient8name.text.toString())
                    editor.putString("ingredient8_quan",viewBinding.myRecipeEdtIngredient8qun.text.toString())
                    editor.putString("ingredient9_title",viewBinding.myRecipeEdtIngredient9name.text.toString())
                    editor.putString("ingredient9_quan",viewBinding.myRecipeEdtIngredient9qun.text.toString())
                    editor.putString("ingredient10_title",viewBinding.myRecipeEdtIngredient10name.text.toString())
                    editor.putString("ingredient10_quan",viewBinding.myRecipeEdtIngredient10qun.text.toString())
                    editor.putString("step1_describe",viewBinding.myRecipeEdtStep.text.toString())
                    editor.putString("step2_describe",viewBinding.myRecipeEdtStep2.text.toString())
                    editor.putString("step3_describe",viewBinding.myRecipeEdtStep3.text.toString())
                    editor.putString("step4_describe",viewBinding.myRecipeEdtStep4.text.toString())
                    editor.putString("step5_describe",viewBinding.myRecipeEdtStep5.text.toString())
                    editor.putString("step6_describe",viewBinding.myRecipeEdtStep6.text.toString())
                    editor.putString("step7_describe",viewBinding.myRecipeEdtStep7.text.toString())
                    editor.putString("step8_describe",viewBinding.myRecipeEdtStep8.text.toString())
                    editor.putString("step9_describe",viewBinding.myRecipeEdtStep9.text.toString())
                    editor.putString("step10_describe",viewBinding.myRecipeEdtStep10.text.toString())
                    editor.apply()


                    //카테고리 선택
                    if (viewBinding.myCoffee.isSelected) {
                        editor.putInt("category", 1)
                        editor.apply()
                    } else if (viewBinding.myBeverage.isSelected) {
                        editor.putInt("category", 2)
                        editor.apply()
                    } else if (viewBinding.myTea.isSelected) {
                        editor.putInt("category", 3)
                        editor.apply()
                    } else if (viewBinding.myAde.isSelected) {
                        editor.putInt("category", 4)
                        editor.apply()
                    } else if (viewBinding.mySmudi.isSelected) {
                        editor.putInt("category", 5)
                        editor.apply()
                    } else if (viewBinding.myHealth.isSelected) {
                        editor.putInt("category", 6)
                        editor.apply()
                    } else {
                        editor.putInt("category", 0)
                        editor.apply()
                    }


                    //업로드하시겠습니까? 다이얼로그 띄우기
                    binding_upload = DialogUploadBinding.inflate(layoutInflater)
                    val dialog_upload_builder = AlertDialog.Builder(this).setView(binding_upload.root)
                    val dialog_upload = dialog_upload_builder.create()

                    //dialog_uploadcategory.window?.setFeatureDrawableResource(ColorDrawable(Color.parseColor()))
                    dialog_upload.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                    dialog_upload.setCanceledOnTouchOutside(true)
                    dialog_upload.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog_upload.window?.setGravity(Gravity.BOTTOM)
                    dialog_upload.setCancelable(true)

                    //업로드 하겠습니다 버튼 눌렀을때
                    binding_upload.myUploadbtn.setOnClickListener {
                        //여기서 api 호출
                        val recipe_list = PostNewRecipeList(
                            sharedPreference.getInt("category", 0),
                            sharedPreference.getString("title", ""),
                            sharedPreference.getString("describe", ""),
                            sharedPreference.getString("aftertip", ""),
                            sharedPreference.getString("time", ""),
                            list[0],
                            sharedPreference.getInt("step",1),
                            sharedPreference.getInt("ingredient",1)
                        )

                        val ingredient_list = arrayListOf<PostNewRecipeIngredient>(
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient1_title", ""),
                                sharedPreference.getString("ingredient1_quan", "")
                            ),
                            PostNewRecipeIngredient(
                                    sharedPreference.getString("ingredient2_title", ""),
                            sharedPreference.getString("ingredient2_quan", "")
                        ),
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient3_title", ""),
                                sharedPreference.getString("ingredient3_quan", "")
                            ),
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient4_title", ""),
                                sharedPreference.getString("ingredient4_quan", "")
                            ),
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient5_title", ""),
                                sharedPreference.getString("ingredient5_quan", "")
                            ),
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient6_title", ""),
                                sharedPreference.getString("ingredient6_quan", "")
                            ),
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient7_title", ""),
                                sharedPreference.getString("ingredient7_quan", "")
                            ),
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient8_title", ""),
                                sharedPreference.getString("ingredient8_quan", "")
                            ),
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient9_title", ""),
                                sharedPreference.getString("ingredient9_quan", "")
                            ),
                            PostNewRecipeIngredient(
                                sharedPreference.getString("ingredient10_title", ""),
                                sharedPreference.getString("ingredient10_quan", "")
                            )
                        )

                        val steps_list = arrayListOf<PostNewRecipeSteps>(
                            PostNewRecipeSteps(
                                1,
                                sharedPreference.getString("step1_describe", ""),
                                list[1]
                            ),
                            PostNewRecipeSteps(
                                2,
                                sharedPreference.getString("step2_describe", ""),
                                list[2]
                            ),
                            PostNewRecipeSteps(
                                3,
                                sharedPreference.getString("step3_describe", ""),
                                list[3]
                            ),
                            PostNewRecipeSteps(
                                4,
                                sharedPreference.getString("step4_describe", ""),
                                list[4]
                            ),
                            PostNewRecipeSteps(
                                5,
                                sharedPreference.getString("step5_describe", ""),
                                list[5]
                            ),
                            PostNewRecipeSteps(
                                6,
                                sharedPreference.getString("step6_describe", ""),
                                list[6]
                            ),
                            PostNewRecipeSteps(
                                7,
                                sharedPreference.getString("step7_describe", ""),
                                list[7]
                            ),
                            PostNewRecipeSteps(
                                8,
                                sharedPreference.getString("step8_describe", ""),
                                list[8]
                            ),
                            PostNewRecipeSteps(
                                9,
                                sharedPreference.getString("step9_describe", ""),
                                list[9]
                            ),
                            PostNewRecipeSteps(
                                10,
                                sharedPreference.getString("step10_describe", ""),
                                list[10]
                            )
                        )

                        val body = PostNewRecipeBody(recipe_list, ingredient_list, steps_list)

                        GlobalScope.launch(Dispatchers.IO) {
                            val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                            token = tokenDb.tokenDao().getToken().token.toString()

                             retrofit.post_newrecipe(token, body)
                                 .enqueue(object : Callback<PostNewRecipeBodyResponse> {
                                     override fun onResponse(
                                         call: Call<PostNewRecipeBodyResponse>,
                                         response: Response<PostNewRecipeBodyResponse>
                                     ) {
                                         Log.d("통신", "통신은 성공임")
                                         var result = response.body()
                                         var isSuccess = result?.success
                                         Log.d("통신", isSuccess.toString())
                                         recipeId = result?.data
                                         Log.d("통신", recipeId.toString())
                                     }

                                     override fun onFailure(
                                         call: Call<PostNewRecipeBodyResponse>,
                                         t: Throwable
                                     ) {
                                         t.message?.let { it1 -> Log.d("통신", it1) }
                                     }
                                 })
                         }

                        //업로드 성공되었습니다 dialog 띄우기
                        dialog_upload.dismiss()
                        binding_uploadsuccess = DialogUploadsuccessBinding.inflate(layoutInflater)
                        val dialog_uploadsuccess_builder = AlertDialog.Builder(this).setView(binding_uploadsuccess.root)
                        val dialog_uploadsuccess = dialog_uploadsuccess_builder.create()

                        dialog_uploadsuccess.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                        dialog_uploadsuccess.setCanceledOnTouchOutside(true)
                        dialog_uploadsuccess.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog_uploadsuccess.window?.setGravity(Gravity.BOTTOM)
                        dialog_uploadsuccess.setCancelable(false)

                        Glide.with(this@MyWritingActivity)
                            .asBitmap()
                            .centerCrop()
                            .load(list[0])
                            .into(binding_uploadsuccess.myUploadimg)
                        Log.d("통신 사진",list[0])

                        editor.clear()
                        editor.apply()

                        //x버튼 눌렀을때
                        binding_uploadsuccess.myXbtn.setOnClickListener {
                            dialog_uploadsuccess.dismiss()
                            finish()
                        }
                        dialog_uploadsuccess.show()
                    }

                    //업로드 안하겠습니다 버튼 눌렀을때
                    binding_upload.myCancelbtn.setOnClickListener {
                        editor.clear()
                        editor.apply()
                        dialog_upload.dismiss()
                    }

                    dialog_upload.show()
                }
            }else{
                //btn 눌러도 아무런 것도 동작하지 않음
                CustomToast.createToast(applicationContext, "모든 항목을 채워주세요")?.show()
            }
        }


        //썸네일 사진 올리기
        viewBinding.myAlbumbtn.setOnClickListener {
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            dialog_camera.show()


            //카메라에서 가져오기
            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 1
                photoURI = Uri.EMPTY
                
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_THUMBNAIL)
                }
                Log.d("카메라 되냐","")
                viewBinding.myImage.bringToFront()

                dialog_camera.dismiss()
            }

            //갤러리에서 가져오기
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(0)
                viewBinding.myImage.bringToFront()
                dialog_camera.dismiss()
            }

            dialog_camera.show()
        }
        //step1 사진 올리기
        viewBinding.myRecipeImageStep.setOnClickListener{

           binding_camera = DialogCameraBinding.inflate(layoutInflater)
           val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
           val dialog_camera = dialog_camera_builder.create()

           dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
           dialog_camera.setCanceledOnTouchOutside(true)
           dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
           dialog_camera.window?.setGravity(Gravity.BOTTOM)
           dialog_camera.setCancelable(true)

           binding_camera.myCameraFrame.setOnClickListener{
               REQUEST_THUMBNAIL = 0
               REQUEST_STEP1 = 1
               
               photoURI = Uri.EMPTY
               fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
               fullSizePictureIntents.resolveActivity(packageManager)?.also {
                   startActivityForResult(fullSizePictureIntents, REQUEST_STEP1)
               }

               viewBinding.myRecipeRealimageStep.bringToFront()
               viewBinding.myRecipeRealimageXbtn.visibility = View.VISIBLE
               viewBinding.myRecipeRealimageXbtn.bringToFront()
               
               dialog_camera.dismiss()
           }
           binding_camera.myFileFrame.setOnClickListener{
               selectGallery(1)
               viewBinding.myRecipeRealimageStep.bringToFront()
               viewBinding.myRecipeRealimageXbtn.visibility = View.VISIBLE
               viewBinding.myRecipeRealimageXbtn.bringToFront()
               dialog_camera.dismiss()
           }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
       }
        //step2 사진 올리기
        viewBinding.myRecipeImageStep2.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 1

                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP2)
                }

                viewBinding.myRecipeRealimageStep2.bringToFront()
                viewBinding.myRecipeRealimageXbtn2.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn2.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(2)
                viewBinding.myRecipeRealimageStep2.bringToFront()
                viewBinding.myRecipeRealimageXbtn2.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn2.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
        }
        //step3 사진 올리기
        viewBinding.myRecipeImageStep3.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 0
                REQUEST_STEP3 = 1

                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP3)
                }

                viewBinding.myRecipeRealimageStep3.bringToFront()
                viewBinding.myRecipeRealimageXbtn3.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn3.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(3)
                viewBinding.myRecipeRealimageStep3.bringToFront()
                viewBinding.myRecipeRealimageXbtn3.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn3.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
            REQUEST_STEP2 = 1
        }
        //step4 사진 올리기
        viewBinding.myRecipeImageStep4.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 0
                REQUEST_STEP3 = 0
                REQUEST_STEP4 = 1

                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP4)
                }

                viewBinding.myRecipeRealimageStep4.bringToFront()
                viewBinding.myRecipeRealimageXbtn4.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn4.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(4)
                viewBinding.myRecipeRealimageStep4.bringToFront()
                viewBinding.myRecipeRealimageXbtn4.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn4.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
            REQUEST_STEP2 = 1
            REQUEST_STEP3 = 1
        }
        //step5 사진 올리기
        viewBinding.myRecipeImageStep5.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 0
                REQUEST_STEP3 = 0
                REQUEST_STEP4 = 0
                REQUEST_STEP5 = 1
                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP5)
                }

                viewBinding.myRecipeRealimageStep5.bringToFront()
                viewBinding.myRecipeRealimageXbtn5.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn5.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(5)
                viewBinding.myRecipeRealimageStep5.bringToFront()
                viewBinding.myRecipeRealimageXbtn5.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn5.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
            REQUEST_STEP2 = 1
            REQUEST_STEP3 = 1
            REQUEST_STEP4 = 1
        }
        //step6 사진 올리기
        viewBinding.myRecipeImageStep6.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 0
                REQUEST_STEP3 = 0
                REQUEST_STEP4 = 0
                REQUEST_STEP5 = 0
                REQUEST_STEP6 = 1

                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP6)
                }

                viewBinding.myRecipeRealimageStep6.bringToFront()
                viewBinding.myRecipeRealimageXbtn6.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn6.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(6)
                viewBinding.myRecipeRealimageStep6.bringToFront()
                viewBinding.myRecipeRealimageXbtn6.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn6.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
            REQUEST_STEP2 = 1
            REQUEST_STEP3 = 1
            REQUEST_STEP4 = 1
            REQUEST_STEP5 = 1
        }
        //step7 사진 올리기
        viewBinding.myRecipeImageStep7.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 0
                REQUEST_STEP3 = 0
                REQUEST_STEP4 = 0
                REQUEST_STEP5 = 0
                REQUEST_STEP6 = 0
                REQUEST_STEP7 = 1

                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP7)
                }

                viewBinding.myRecipeRealimageStep7.bringToFront()
                viewBinding.myRecipeRealimageXbtn7.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn7.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(7)
                viewBinding.myRecipeRealimageStep7.bringToFront()
                viewBinding.myRecipeRealimageXbtn7.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn7.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
            REQUEST_STEP2 = 1
            REQUEST_STEP3 = 1
            REQUEST_STEP4 = 1
            REQUEST_STEP5 = 1
            REQUEST_STEP6 = 1
        }
        //step8 사진 올리기
        viewBinding.myRecipeImageStep8.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 0
                REQUEST_STEP3 = 0
                REQUEST_STEP4 = 0
                REQUEST_STEP5 = 0
                REQUEST_STEP6 = 0
                REQUEST_STEP7 = 0
                REQUEST_STEP8 = 1

                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP8)
                }

                viewBinding.myRecipeRealimageStep8.bringToFront()
                viewBinding.myRecipeRealimageXbtn8.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn8.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(8)
                viewBinding.myRecipeRealimageStep8.bringToFront()
                viewBinding.myRecipeRealimageXbtn8.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn8.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
            REQUEST_STEP2 = 1
            REQUEST_STEP3 = 1
            REQUEST_STEP4 = 1
            REQUEST_STEP5 = 1
            REQUEST_STEP6 = 1
            REQUEST_STEP7 = 1
        }
        //step9 사진 올리기
        viewBinding.myRecipeImageStep9.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 0
                REQUEST_STEP3 = 0
                REQUEST_STEP4 = 0
                REQUEST_STEP5 = 0
                REQUEST_STEP6 = 0
                REQUEST_STEP7 = 0
                REQUEST_STEP8 = 0
                REQUEST_STEP9 = 1

                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP9)
                }

                viewBinding.myRecipeRealimageStep9.bringToFront()
                viewBinding.myRecipeRealimageXbtn9.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn9.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(9)
                viewBinding.myRecipeRealimageStep9.bringToFront()
                viewBinding.myRecipeRealimageXbtn9.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn9.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
            REQUEST_STEP2 = 1
            REQUEST_STEP3 = 1
            REQUEST_STEP4 = 1
            REQUEST_STEP5 = 1
            REQUEST_STEP6 = 1
            REQUEST_STEP7 = 1
            REQUEST_STEP8 = 1
        }
        //step10 사진 올리기
        viewBinding.myRecipeImageStep10.setOnClickListener{
            binding_camera = DialogCameraBinding.inflate(layoutInflater)
            val dialog_camera_builder = AlertDialog.Builder(this).setView(binding_camera.root)
            val dialog_camera = dialog_camera_builder.create()

            dialog_camera.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_camera.setCanceledOnTouchOutside(true)
            dialog_camera.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_camera.window?.setGravity(Gravity.BOTTOM)
            dialog_camera.setCancelable(true)

            binding_camera.myCameraFrame.setOnClickListener{
                REQUEST_THUMBNAIL = 0
                REQUEST_STEP1 = 0
                REQUEST_STEP2 = 0
                REQUEST_STEP3 = 0
                REQUEST_STEP4 = 0
                REQUEST_STEP5 = 0
                REQUEST_STEP6 = 0
                REQUEST_STEP7 = 0
                REQUEST_STEP8 = 0
                REQUEST_STEP9 = 0
                REQUEST_STEP10 = 1

                photoURI = Uri.EMPTY
                fullSizePictureIntents = getPictureIntent_App_Specific(applicationContext)
                fullSizePictureIntents.resolveActivity(packageManager)?.also {
                    startActivityForResult(fullSizePictureIntents, REQUEST_STEP10)
                }

                viewBinding.myRecipeRealimageStep10.bringToFront()
                viewBinding.myRecipeRealimageXbtn10.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn10.bringToFront()

                dialog_camera.dismiss()
            }
            binding_camera.myFileFrame.setOnClickListener{
                selectGallery(10)
                viewBinding.myRecipeRealimageStep10.bringToFront()
                viewBinding.myRecipeRealimageXbtn10.visibility = View.VISIBLE
                viewBinding.myRecipeRealimageXbtn10.bringToFront()
                dialog_camera.dismiss()
            }
            dialog_camera.show()
            REQUEST_THUMBNAIL = 1
            REQUEST_STEP1 = 1
            REQUEST_STEP2 = 1
            REQUEST_STEP3 = 1
            REQUEST_STEP4 = 1
            REQUEST_STEP5 = 1
            REQUEST_STEP6 = 1
            REQUEST_STEP7 = 1
            REQUEST_STEP8 = 1
            REQUEST_STEP9 = 1
        }
    }


    private val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageUri = result.data?.data ?: return@registerForActivityResult
            Log.d("갤러리 확인","${imageUri}")
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            //썸네일 post


            GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()

                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list[0] = data.toString()
                                    Log.d("통신", data.toString())
                                    Log.d("통신 리스트", list[0])

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }

            //val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)

            Glide.with(this)
                .asBitmap()
                .centerCrop()
                .load(imageUri)
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myImage)

           /*object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val layout = viewBinding.myImage
                        layout.background = BitmapDrawable(resources, resource)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                }*/
        }
    }
    private val imageResult1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step1 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()

                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list[1] = data.toString()
                                Log.d("통신", data.toString())
                                Log.d("통신 리스트", list[1])

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
            }

            Glide.with(this)
                .asBitmap()
                .centerCrop()
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .load(imageUri)
                .into(viewBinding.myRecipeRealimageStep)
        }
    }
    private val imageResult2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step2 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list[2] = data.toString()
                                Log.d("통신", data.toString())
                                Log.d("통신 리스트", list[2])

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
                            }


            Glide.with(this)
                .asBitmap()
                .centerCrop()
                .load(imageUri)
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myRecipeRealimageStep2)
        }
    }
    private val imageResult3 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step3 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list[3] = data.toString()
                                Log.d("통신", data.toString())

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
            }

            Glide.with(this)
                .asBitmap()
                .centerCrop()
                .load(imageUri)
                .apply(
                    RequestOptions()
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myRecipeRealimageStep3)
        }
    }
    private val imageResult4 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step4 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list.set(4, data.toString())
                                Log.d("통신", data.toString())

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
            }


            Glide.with(this)
                .asBitmap()
                .centerCrop()
                .load(imageUri)
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myRecipeRealimageStep4)
        }
    }
    private val imageResult5 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult

            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step5 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list.set(5, data.toString())
                                Log.d("통신", data.toString())

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
            }


            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .centerCrop()
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into( viewBinding.myRecipeRealimageStep5)
        }
    }
    private val imageResult6 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step6 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list.set(6, data.toString())
                                Log.d("통신", data.toString())

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
                        }


            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .centerCrop()
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myRecipeRealimageStep6)
        }
    }
    private val imageResult7 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step7 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list.set(7, data.toString())
                                Log.d("통신", data.toString())

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
            }


            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .centerCrop()
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myRecipeRealimageStep7)
        }
    }
    private val imageResult8 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step8 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list.set(8, data.toString())
                                Log.d("통신", data.toString())

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
            }


            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .centerCrop()
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myRecipeRealimageStep8)
        }
    }
    private val imageResult9 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step9 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list.set(9, data.toString())
                                Log.d("통신", data.toString())

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
            }

            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .centerCrop()
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myRecipeRealimageStep9)
        }
    }
    private val imageResult10 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            val file = File(absolutelyPath(imageUri, this))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)

            // step10 post
            GlobalScope.launch(Dispatchers.IO) {
                val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                token = tokenDb.tokenDao().getToken().token.toString()
                retrofit.post_newrecipe_image(token, body)
                    .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                        override fun onResponse(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            response: Response<PostNewRecipeImageBodyResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("통신", "이미지 전송 성공")

                                val result = response.body()
                                val data = result?.image?.image
                                list.set(10, data.toString())
                                Log.d("통신", data.toString())

                            } else {
                                Log.d("통신", "이미지 전송 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNewRecipeImageBodyResponse>,
                            t: Throwable
                        ) {
                            Log.d("통신", t.message.toString())
                        }
                    })
            }


            Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .centerCrop()
                .apply(
                    RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(viewBinding.myRecipeRealimageStep10)
        }
    }

    private fun selectGallery(num: Int){
        val writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

//        if(writePermission == PackageManager.PERMISSION_DENIED ||
//            readPermission == PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE), REQ_GALLERY)
//        }else{
        Log.d("퍼미션 확인","${writePermission}")
        Log.d("퍼미션 확인","${readPermission}")
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )

            if(num == 1){
                imageResult1.launch(intent)
            }else if(num == 2){
                imageResult2.launch(intent)
            } else if(num == 3){
                imageResult3.launch(intent)
            }else if(num == 4){
                imageResult4.launch(intent)
            }else if(num == 5){
                imageResult5.launch(intent)
            }else if(num == 6){
                imageResult6.launch(intent)
            }else if(num == 7){
                imageResult7.launch(intent)
            }else if(num == 8){
                imageResult8.launch(intent)
            }else if(num == 9){
                imageResult9.launch(intent)
            }else if(num == 10){
                imageResult10.launch(intent)
            } else{
                imageResult.launch(intent)
            }
//        }
    }
    companion object{
        const val REQ_GALLERY =1
    }
    fun absolutelyPath(path: Uri?, context : Context): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        Log.d("갤러리 projection 확인", proj.toString())
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)

        return result!!
    }
   /* private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        Log.d("카메라 projection 확인", proj.toString())
        val cursor = contentResolver.query(contentURI, proj, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            //cursor.moveToNext()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }*/


    private var REQUEST_THUMBNAIL = 1
    private var REQUEST_STEP1 = 1
    private var REQUEST_STEP2 = 1
    private var REQUEST_STEP3 = 1
    private var REQUEST_STEP4 = 1
    private var REQUEST_STEP5 = 1
    private var REQUEST_STEP6 = 1
    private var REQUEST_STEP7 = 1
    private var REQUEST_STEP8 = 1
    private var REQUEST_STEP9 = 1
    private var REQUEST_STEP10 = 1
    //허용안할경우에 재확인..?
    private fun requestPermission(){
        Log.d("카메라권한","request 왜 안돼ㅠㅠ")
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),1)
    }
    //카메라 허용이 됐는지 안됐는지 확인
    private fun checkPermission():Boolean{
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }
    //빈파일 생성
    @Throws(IOException::class)
    fun createImageFile(storageDir: File?): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            Log.i("syTest", "Created File AbsolutePath : $absolutePath")
        }
    }
    //file 생성하고, 생성된 file로부터 uri 생성
    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun getPictureIntent_App_Specific(context: Context): Intent {
        val fullSizeCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //1) File 생성 - 촬영 사진이 저장 될
        //photoFile 경로 = /storage/emulated/0/Android/data/패키지명/files/Pictures/
        val photoFile: File? = try {
            createImageFile(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        } catch (ex: IOException) {
            // Error occurred while creating the File
            ex.printStackTrace()
            null
        }
        Log.d("확인 file 생성, 글쓰기", photoFile.toString())

        photoFile?.also {
            //2) 생성된 File로 부터 Uri 생성 (by FileProvider)
            //URI 형식 EX) content://com.example.img.fileprovider/cameraImg/JPEG_20211124_202832_6573897384086993610.jpg
            photoURI = FileProvider.getUriForFile(
                context,
                "com.example.umc_zipdabang" + ".fileprovider",
                it
            )
            a = photoURI
            Log.d("확인 생성된 file로부터 uri 생성, 글쓰기", photoURI.toString())

            //3) 생성된 Uri를 Intent에 Put
            fullSizeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        GlobalScope.launch(Dispatchers.IO) {
            delay(100)
        }
        return fullSizeCaptureIntent





    }
    // 절대경로 파악할 때 사용된 메소드
    fun createCopyAndReturnRealPath(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.getContentResolver() ?: return null

        // 파일 경로를 만듬
        val filePath: String = (context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            // 매개변수로 받은 uri 를 통해  이미지에 필요한 데이터를 불러 들인다.
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file.getAbsolutePath()
    }


    //uri 변수명
    lateinit var photoURI: Uri

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("카메라 확인0", "1")
        if( resultCode == Activity.RESULT_OK) {
            Log.d("카메라 확인0", "2")
            if( requestCode == REQUEST_THUMBNAIL)
            {
                Log.d("카메라 확인0", "3")
                val imageUri = a

                Log.d("카메라 확인 phtouri","${imageUri}")

                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                Log.d("카메라 확인 1","${filePath}")
                val file = File(filePath)
                Log.d("카메라 확인 2","${file}")

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                Log.d("카메라 확인 3","${inputStream}")
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                Log.d("카메라 확인 4","${outputStream}")
                val buf = ByteArray(1024)
                Log.d("카메라 확인 5","${buf}")
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()
                

                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                Log.d("카메라 확인 5","${filee}")
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                Log.d("카메라 확인 6","${requestFile}")
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)
                Log.d("카메라 확인 7","${body}")
                

                // thumbnail post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()


                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image

                                    list.set(0, data.toString())
                                    Log.d("통신", list[0])
                                    
                                    viewBinding.myImage.bringToFront()

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[0])
                                        .centerCrop()
                                        .into(viewBinding.myImage)
                                    Log.d("카메라확인","8")

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }

                /*val imageBitmap :Bitmap? = data?.extras?.get("data") as Bitmap?
                viewBinding.myImage.setImageBitmap(imageBitmap)
                editor2.putString("thumbnail", imageBitmap.toString())
                editor2.apply()
                sharedPreference2.getString("thumbnail", "@")?.let { Log.e(ContentValues.TAG, it) }*/

            } else if(requestCode == REQUEST_STEP1)
            {
                
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)

                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()
                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image

                                    list.set(1, data.toString())
                                    Log.d("통신", list[1])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[1])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep)

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }


            } else if(requestCode == REQUEST_STEP2)
            {

                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)

                // step2 post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()
                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list.set(2, data.toString())
                                    Log.d("통신", list[2])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[2])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep2)

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }

            }else if(requestCode == REQUEST_STEP3)
            {
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)

                // step3 post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()

                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list.set(3, data.toString())
                                    Log.d("통신", list[3])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[3])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep3)

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })

                }
            }else if(requestCode == REQUEST_STEP4)
            {
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)


                // step4 post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()
                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list.set(4, data.toString())
                                    Log.d("통신", list[4])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[4])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep4)

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }
            }else if(requestCode == REQUEST_STEP5)
            {
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)


                // step5 post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()
                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list.set(5, data.toString())
                                    Log.d("통신", list[5])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[5])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep5)

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }
            }else if(requestCode == REQUEST_STEP6)
            {
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)

                // step6 post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()
                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list.set(6, data.toString())
                                    Log.d("통신", list[6])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[6])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep6)

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }

            }else if(requestCode == REQUEST_STEP7)
            {
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)

                // step7 post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()
                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list.set(7, data.toString())
                                    Log.d("통신", list[7])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[7])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep7)

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }
            }else if(requestCode == REQUEST_STEP8)
            {
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)


                // step8 post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()
                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list.set(8, data.toString())
                                    Log.d("통신", list[8])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[8])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep8)

                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }
            }else if(requestCode == REQUEST_STEP9)
            {
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)

                // step9 post
                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()
                    retrofit.post_newrecipe_image(token, body)
                        .enqueue(object : Callback<PostNewRecipeImageBodyResponse> {
                            override fun onResponse(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                response: Response<PostNewRecipeImageBodyResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("통신", "이미지 전송 성공")

                                    val result = response.body()
                                    val data = result?.image?.image
                                    list.set(9, data.toString())
                                    Log.d("통신", list[9])

                                    Glide.with(this@MyWritingActivity)
                                        .load(list[9])
                                        .centerCrop()
                                        .into(viewBinding.myRecipeRealimageStep9)
                                } else {
                                    Log.d("통신", "이미지 전송 실패")
                                }
                            }

                            override fun onFailure(
                                call: Call<PostNewRecipeImageBodyResponse>,
                                t: Throwable
                            ) {
                                Log.d("통신", t.message.toString())
                            }
                        })
                }
            }else if(requestCode == REQUEST_STEP10)
            {
                val imageUri = a
                val filePath: String = (this@MyWritingActivity.getApplicationInfo().dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
                val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
                // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                if (inputStream != null) {
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream?.close()

                Log.d("카메라 확인","${imageUri}")
                //file 객체 만들어준다. 파일의 경로를 가져와야 한다.
                val filee = File(file.getAbsolutePath())
                //requestbody 객체로 변환한다.
                val requestFile = filee.asRequestBody("image/*".toMediaTypeOrNull())
                //maltipart.Part로 변환해준다.
                val body = MultipartBody.Part.createFormData("img", filee.name, requestFile)

                // step10 post

                GlobalScope.launch(Dispatchers.IO) {
                    val tokenDb = TokenDatabase.getTokenDatabase(this@MyWritingActivity)
                    token = tokenDb.tokenDao().getToken().token.toString()

                retrofit.post_newrecipe_image(token, body).enqueue(object: Callback<PostNewRecipeImageBodyResponse>{
                    override fun onResponse(call: Call<PostNewRecipeImageBodyResponse>, response: Response<PostNewRecipeImageBodyResponse>) {
                        if(response.isSuccessful){
                            Log.d("통신","이미지 전송 성공")

                            val result = response.body()
                            val data = result?.image?.image
                            list.set(10, data.toString())
                            Log.d("통신",list[10])

                            Glide.with(this@MyWritingActivity)
                                .load(list[10])
                                .centerCrop()
                                .into(viewBinding.myRecipeRealimageStep10)

                        }else{
                            Log.d("통신","이미지 전송 실패")
                        }
                    }
                    override fun onFailure(call: Call<PostNewRecipeImageBodyResponse>, t: Throwable) {
                        Log.d("통신",t.message.toString())
                    }
                })
                }
            }
        }
    }

    override fun onBackPressed() {
        if(back){
            val intent=Intent(this,HomeMainActivity::class.java)
            intent.putExtra("GOBACK","GOBACK")
            startActivity(intent)
        }
        super.onBackPressed()
    }

}

 /*override fun onBackPressed() {

           binding_reallynotsave = DialogReallynotsaveBinding.inflate(layoutInflater)
           val dialog_reallynotsave_builder = AlertDialog.Builder(this).setView(binding_reallynotsave.root)
           val dialog_reallynotsave = dialog_reallynotsave_builder.create()

           dialog_reallynotsave.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
           dialog_reallynotsave.setCanceledOnTouchOutside(true)
           dialog_reallynotsave.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
           dialog_reallynotsave.window?.setGravity(Gravity.BOTTOM)
           dialog_reallynotsave.setCancelable(true)

           //레시피 쓰던거 삭제하겠다
           binding_reallynotsave.myDeletebtn.setOnClickListener{
               dialog_reallynotsave.dismiss()

               binding_notsave = DialogNotsaveBinding.inflate(layoutInflater)
               val dialog_notsave_builder = AlertDialog.Builder(this).setView(binding_notsave.root)
               val dialog_notsave = dialog_notsave_builder.create()

               dialog_notsave.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
               dialog_notsave.setCanceledOnTouchOutside(true)
               dialog_notsave.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
               dialog_notsave.window?.setGravity(Gravity.BOTTOM)
               dialog_notsave.setCancelable(true)

               //취소 버튼 눌렀을때
               binding_notsave.myCancelbtn.setOnClickListener{
                   dialog_notsave.onBackPressed()
               }
               //삭제하기 버튼 눌렀을때
               binding_notsave.myDeletebtn.setOnClickListener{
                   CustomToast.createToast(applicationContext, "작성 중인 레시피를 삭제하였어요")?.show()

                   finish()
               }
               dialog_notsave.show()
           }
           //레시피 쓰던거 삭제안할거임
           binding_reallynotsave.myCancelbtn.setOnClickListener {
               dialog_reallynotsave.onBackPressed()
               dialog_reallynotsave.dismiss()
           }

           dialog_reallynotsave.show()
   }*/

