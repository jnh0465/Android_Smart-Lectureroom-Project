package com.jiwoolee.android_smartlectureroom.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.model.IMyService
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jiwoolee.android_smartlectureroom.base.FragmentStatePagerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit

class FragmentActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    private val TOPIC = "send" //fcm firebase 토픽 선언

    private var disposable: CompositeDisposable? = CompositeDisposable()
    private val retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)
    val fragmentAdapter: FragmentStatePagerAdapter by lazy { FragmentStatePagerAdapter(3, supportFragmentManager) }    // MainAdapter를 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        mContext = this

        fragment_container.adapter = fragmentAdapter
        fragment_container.offscreenPageLimit = 3 // PageLimit 지정

        btn_homefragment.setOnClickListener(this) //리스너 연결
        btn_schedulefragment.setOnClickListener(this)
        btn_third.setOnClickListener(this)

        val isFirst = SharedPreferenceManager.getToken(mContext, "PREFFIRST")
        if (!isFirst) {                                                      //최초 실행시
            getToken()
            tokenUpdate()
            SharedPreferenceManager.setToken(mContext, "PREFFIRST", true)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_homefragment -> fragment_container.currentItem = 0
            R.id.btn_schedulefragment -> fragment_container.currentItem = 1
            R.id.btn_third -> fragment_container.currentItem = 2
        }
    }

    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@FragmentActivity) { instanceIdResult ->
            val token = instanceIdResult.token  //토큰 생성
            SharedPreferenceManager.setString(mContext, "PREFTOKEN", token)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)  //알림허용
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

    open fun getAttendStateProcess() {
        disposable!!.add(iMyService!!.getAttendStateProcess(SharedPreferenceManager.getString(mContext, "PREFID")!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    SharedPreferenceManager.setString(mContext, "PREFAS", response)
                }
        )
    }

    override fun onBackPressed() { } //뒤로가기 막기
}
