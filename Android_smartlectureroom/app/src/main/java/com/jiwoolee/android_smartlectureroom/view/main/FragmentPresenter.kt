package com.jiwoolee.android_smartlectureroom.view.main

import android.os.Handler
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.jiwoolee.android_smartlectureroom.base.FragmentStatePagerAdapter
import com.jiwoolee.android_smartlectureroom.base.RecyclerviewAdapter
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.model.Data
import com.jiwoolee.android_smartlectureroom.model.IMyService
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Retrofit
import java.util.ArrayList

open class FragmentPresenter internal constructor() : FragmentContract.Presenter {
    private var view: FragmentContract.View? = null

    private var disposable: CompositeDisposable? = CompositeDisposable()
    private var retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)
    private var adapter: RecyclerviewAdapter? = null

    private var classList: ArrayList<String> = ArrayList() //ArrayList 정의
    private var attendList: ArrayList<String> = ArrayList()
    private var nameList: ArrayList<String> = ArrayList()
    private var dayList: ArrayList<String> = ArrayList()
    private var timeList: ArrayList<String> = ArrayList()
    private var idList: ArrayList<String> = ArrayList()
    private var lectureList: ArrayList<String> = ArrayList()
    private var lectureroomList: ArrayList<String> = ArrayList()
    private var scheduleList: ArrayList<String> = ArrayList()

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun setView(view: FragmentContract.View?) {
        this.view = view
    }

    override fun releaseView() {
        disposable?.clear()
    }

    //HomeFragment/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun isFirst(fragmentAdapter: FragmentStatePagerAdapter, viewpager : ViewPager) {
        initViewpagerAdapter(fragmentAdapter, viewpager)

        val isFirst = SharedPreferenceManager.getToken(FragmentActivity.mContext, "PREFFIRST")  //최초 실행시
        if (!isFirst) {
            view?.getToken()
            updateToken()
            Handler().postDelayed({
                viewpager.adapter!!.notifyDataSetChanged() //새로고침
            }, 300)
        }
    }

    //Token
    override fun updateToken() {
        android.os.Handler().postDelayed(
                {
                    disposable!!.add(iMyService!!.sendToken(SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFID")!!, SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFTOKEN")!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { response ->
                                //Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show(); //node 서버에서 response.json으로 보낸 응답 받아서 toast
                                when (response) {
                                    "1" -> //로그인 성공시
                                        Toast.makeText(FragmentActivity.mContext, "토큰이 등록되었습니다" + SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFTOKEN")!!, Toast.LENGTH_SHORT).show()
                                    "2" -> Toast.makeText(FragmentActivity.mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                                    "0" -> Toast.makeText(FragmentActivity.mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    )
                    SharedPreferenceManager.setToken(FragmentActivity.mContext, "PREFFIRST", true)
                },
                3000)
    }


    override fun getAttendStateProcess() {
        disposable!!.add(iMyService!!.getAttendStateProcess(SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFID")!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    SharedPreferenceManager.setString(FragmentActivity.mContext, "PREFAS", response)
                }
        )
    }

    //Viewpager
    override fun initViewpagerAdapter(fragmentAdapter : FragmentStatePagerAdapter, viewpager : ViewPager){
        viewpager.adapter = fragmentAdapter //adapter 연결
        viewpager.offscreenPageLimit = 3    //pageLimit 지정
        viewpager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                viewpager.adapter!!.notifyDataSetChanged() //새로고침
            }
        })
    }

    //Recyclerview
    override fun setRecyclerview(recyclerview: RecyclerView){
        initRecyclerview(recyclerview)
        refineAttenddata()
        adapter!!.notifyDataSetChanged()
    }

    override fun initRecyclerview(recyclerview: RecyclerView){
        val linearLayoutManager = LinearLayoutManager(FragmentActivity.mContext)
        recyclerview.layoutManager = linearLayoutManager

        adapter = RecyclerviewAdapter()
        recyclerview.adapter = adapter
    }

    override fun refineAttenddata(){
        val str:String = SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFAS").toString() //node서버에서 받아온 값 저장
        val array = str.split("a")
        for (x in 1 until array.size step 2){
            val arrayDetail = array[x].split(",")

            for(y in 1 until arrayDetail.size step 4)  classList.add(arrayDetail[y])        //교시
            for(y in 2 until arrayDetail.size step 4)  attendList.add(arrayDetail[y])       //출결
            for(y in 3 until arrayDetail.size step 4)  nameList.add(arrayDetail[y])         //과목명
        }

        for (i in classList.indices) {
            val data = Data()
            data.title = nameList[i]
            data.content = classList[i]+"교시 "+attendList[i]
            adapter!!.addItem(data)
        }
    }

    //ScheduleFragment/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun refineScheduledata() {

    }

    override fun drawSchedule(){

    }

    override fun getSchedule(){
        disposable!!.add(iMyService!!.getSchedule(SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFID")!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    SharedPreferenceManager.setString(FragmentActivity.mContext, "PREFSC", response)
                }
        )
    }
}
