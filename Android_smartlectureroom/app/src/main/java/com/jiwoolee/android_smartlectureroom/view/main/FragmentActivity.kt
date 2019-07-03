package com.jiwoolee.android_smartlectureroom.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.jiwoolee.android_smartlectureroom.ThirdFragment
import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.base.FragmentAdapter
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.model.IMyService
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

class FragmentActivity : AppCompatActivity() {
    private val TOPIC = "send" //fcm firebase 토픽 선언

    private var disposable: CompositeDisposable? = CompositeDisposable()
    private val retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)

    private var mViewPager: ViewPager? = null
    private var adapter = FragmentAdapter(supportFragmentManager)

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        mContext = this

        mViewPager = findViewById<View>(R.id.container) as ViewPager
        setupViewPager(mViewPager)

        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        val isFirst = SharedPreferenceManager.getToken(mContext, "PREFFIRST")
        if (!isFirst) {                                                      //최초 실행시
            getToken()
            tokenUpdate()
            SharedPreferenceManager.setToken(mContext, "PREFFIRST", true)
        }
    }

    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@FragmentActivity) { instanceIdResult ->
            val token = instanceIdResult.token  //토큰 생성
            SharedPreferenceManager.setString(mContext, "PREFTOKEN", token)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)  //알림허용
    }

    override fun onBackPressed() { //뒤로가기 막기
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        adapter.addFragment(HomeFragment(), "홈")
        adapter.addFragment(ScheduleFragment(), "시간표")
        adapter.addFragment(ThirdFragment(), "?")
        viewPager!!.adapter = adapter
    }

    private fun tokenUpdate() {
        android.os.Handler().postDelayed(
                {
                    disposable!!.add(iMyService!!.sendToken(SharedPreferenceManager.getString(mContext, "PREFID")!!, SharedPreferenceManager.getString(mContext, "PREFTOKEN")!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { response ->
                                //Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show(); //node 서버에서 response.json으로 보낸 응답 받아서 toast
                                when (response) {
                                    "1" -> //로그인 성공시
                                        Toast.makeText(mContext, "토큰이 등록되었습니다" + SharedPreferenceManager.getString(mContext, "PREFTOKEN")!!, Toast.LENGTH_SHORT).show()
                                    "2" -> Toast.makeText(mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                                    "0" -> Toast.makeText(mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    )
                },
                3000)
    }

    open fun getSchedule() {
        disposable!!.add(iMyService!!.getSchedule(SharedPreferenceManager.getString(mContext, "PREFID")!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    SharedPreferenceManager.setString(mContext, "PREFSC", response)
                }
        )
    }
}
