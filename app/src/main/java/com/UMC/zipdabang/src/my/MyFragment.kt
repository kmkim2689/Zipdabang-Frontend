package com.UMC.zipdabang.src.my

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.UMC.zipdabang.databinding.FragmentMyBinding
import com.UMC.zipdabang.config.src.main.Home.HomeMainActivity
import com.UMC.zipdabang.config.src.main.Home.Scrap.MyScapActivity
import com.UMC.zipdabang.config.src.main.Jip.src.main.roomDb.TokenDatabase

import com.UMC.zipdabang.src.my.data.IntroChallengedoneRVAdapter
import com.UMC.zipdabang.src.my.data.IntroChallengingRVAdapter
import com.UMC.zipdabang.src.my.data.ItemRecipeRVAdapter
import com.UMC.zipdabang.src.setting.MySettingActivity

import com.UMC.zipdabang.src.my.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyFragment : Fragment(){

    lateinit var viewBinding: FragmentMyBinding
    var token: String = " "

    lateinit var mainActivity: HomeMainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as HomeMainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding= FragmentMyBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    private val retrofit = RetrofitInstance.getInstance().create(APIS_My::class.java)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //도전중, 도전완료, 마이스크랩 2개씩 띄우기
        GlobalScope.launch(Dispatchers.IO){
            //viewBinding.etSearch.setText("")
            val tokenDb = TokenDatabase.getTokenDatabase(activity as HomeMainActivity)
            token = tokenDb.tokenDao().getToken().token.toString()

            retrofit.get_recipe_two(token).enqueue(object: Callback<GetRecipeTwoResponse>{
                override fun onResponse(
                    call: Call<GetRecipeTwoResponse>,
                    response: Response<GetRecipeTwoResponse>
                ) {


                    var challenging: ArrayList<ItemRecipeChallengeData> = arrayListOf()
                    var complete: ArrayList<ItemRecipeChallengeData> = arrayListOf()
                    var scrap: ArrayList<ItemRecipeChallengeData> = arrayListOf()

                    val result = response.body()
                    Log.d("통신","${result}")
                    Log.d("통신","${token}")



                    //도전중
                    if(result?.data?.myChallengingOverView?.size != 0){
                        challenging.add(
                            ItemRecipeChallengeData(
                                result?.data?.myChallengingOverView?.get(0)?.recipeId,
                                result?.data?.myChallengingOverView?.get(0)?.likes,
                                result?.data?.myChallengingOverView?.get(0)?.image,
                                result?.data?.myChallengingOverView?.get(0)?.name,
                            )
                        )
                        if(result?.data?.myChallengingOverView?.size != 1){
                            challenging.add(
                                ItemRecipeChallengeData(
                                    result?.data?.myChallengingOverView?.get(1)?.recipeId,
                                    result?.data?.myChallengingOverView?.get(1)?.likes,
                                    result?.data?.myChallengingOverView?.get(1)?.image,
                                    result?.data?.myChallengingOverView?.get(1)?.name,
                                )
                            )
                        }
                    }

                    //도전완료
                    if(result?.data?.myCompleteOverView?.size !=0){
                        complete.add(
                            ItemRecipeChallengeData(
                                result?.data?.myCompleteOverView?.get(0)?.recipeId,
                                result?.data?.myCompleteOverView?.get(0)?.likes,
                                result?.data?.myCompleteOverView?.get(0)?.image,
                                result?.data?.myCompleteOverView?.get(0)?.name,
                            )
                        )

                        if(result?.data?.myCompleteOverView?.size !=1){
                            complete.add(
                                ItemRecipeChallengeData(
                                    result?.data?.myCompleteOverView?.get(1)?.recipeId,
                                    result?.data?.myCompleteOverView?.get(1)?.likes,
                                    result?.data?.myCompleteOverView?.get(1)?.image,
                                    result?.data?.myCompleteOverView?.get(1)?.name,
                                )
                            )

                        }
                    }
                    //내스크랩
                    if(result?.data?.myScrapOverView?.size != 0){
                        scrap.add(
                            ItemRecipeChallengeData(
                                result?.data?.myScrapOverView?.get(0)?.recipeId,
                                result?.data?.myScrapOverView?.get(0)?.likes,
                                result?.data?.myScrapOverView?.get(0)?.image,
                                result?.data?.myScrapOverView?.get(0)?.name,
                            )
                        )

                        if(result?.data?.myScrapOverView?.size != 1){
                            scrap.add(
                                ItemRecipeChallengeData(
                                    result?.data?.myScrapOverView?.get(1)?.recipeId,
                                    result?.data?.myScrapOverView?.get(1)?.likes,
                                    result?.data?.myScrapOverView?.get(1)?.image,
                                    result?.data?.myScrapOverView?.get(1)?.name,
                                )
                            )
                        }
                    }


                    viewBinding.myRvChallenging.layoutManager = GridLayoutManager(context, 2)
                    val adapter1 = IntroChallengingRVAdapter(challenging)
                    viewBinding.myRvChallenging.adapter = adapter1
                    adapter1.notifyDataSetChanged()

                    viewBinding.myRvChallengedone.layoutManager = GridLayoutManager(context, 2)
                    val adapter2 = IntroChallengedoneRVAdapter(complete)
                    viewBinding.myRvChallengedone.adapter = adapter2
                    adapter2.notifyDataSetChanged()

                    viewBinding.myRvMyscrap.layoutManager = GridLayoutManager(context, 2)
                    val adapter3 = ItemRecipeRVAdapter(scrap)
                    viewBinding.myRvMyscrap.adapter = adapter3
                    adapter3.notifyDataSetChanged()

                        /////
                   // adapter1.setOnItemClickListener1(object: IntroChallengingRVAdapter)

                }

                override fun onFailure(call: Call<GetRecipeTwoResponse>, t: Throwable) {
                    Log.d("통신", "통신 실패")
                }
            })
        }



        viewBinding.myBtnChallenging.setOnClickListener {
            /*parentFragmentManager.beginTransaction()
                .replace(R.id.mainfragmentcontainer, MyChallengingFragment())
                .addToBackStack(null)
                .commit()*/
            val intent = Intent(activity, MyChallengingActivity::class.java)
            startActivity(intent)
        }
        viewBinding.myBtnChallengedone.setOnClickListener {
            /*parentFragmentManager.beginTransaction()
                .replace(R.id.mainfragmentcontainer, MyChallengedoneFragment())
                .addToBackStack(null)
                .commit()*/
            val intent = Intent(activity, MyChallengedoneActivity::class.java)
            startActivity(intent)
        }
        viewBinding.myBtnMyscrap.setOnClickListener {
            val intent = Intent(activity, MyScapActivity::class.java)
            startActivity(intent)
        }


        viewBinding.myBtnIcon1.setOnClickListener {
            val intent = Intent(activity, MyWritingActivity::class.java)
            startActivity(intent)
        }
        viewBinding.myBtnIcon2.setOnClickListener {
            val intent = Intent(activity, MySaveActivity::class.java)
            startActivity(intent)
        }
        viewBinding.myBtnIcon3.setOnClickListener {
            /*parentFragmentManager.beginTransaction()
                .replace(R.id.mainfragmentcontainer, MyMyrecipeFragment())
                .addToBackStack(null)
                .commit()*/
            val intent = Intent(activity, MyMyrecipeActivity::class.java)
            startActivity(intent)
        }
       //프로필설정 버튼 눌렀을떄 리스너->하현과 연결
        viewBinding.myBtnIcon4.setOnClickListener {
            val intent = Intent(activity, MySettingActivity::class.java)
            startActivity(intent)
        }
    }
}