package com.nuntteuniachim.sroomi.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.nuntteuniachim.sroomi.base.SharedPreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.base.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_fragment.*

class FragmentActivity : AppCompatActivity(), FragmentContract.View, View.OnClickListener {
    private val presenter = FragmentPresenter()
    private val TOPIC = "send" //fcm firebase 토픽 선언
    private val fragmentAdapter: FragmentStatePagerAdapter by lazy { FragmentStatePagerAdapter(3, supportFragmentManager) }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        mContext = this

        presenter.setView(this) // presenter 연결

        btn_homefragment.setOnClickListener(this) //리스너 연결
        btn_schedulefragment.setOnClickListener(this)
        btn_third.setOnClickListener(this)

        val viewpager = findViewById<View>(R.id.fragment_container) as ViewPager
        presenter.isFirst(fragmentAdapter, viewpager)

        Log.d("aaaaaaaaaaaaaaaaaaaaaaaaa", SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFTOKEN"))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_homefragment -> fragment_container.currentItem = 0
            R.id.btn_schedulefragment -> fragment_container.currentItem = 1
            R.id.btn_third -> fragment_container.currentItem = 2
        }
    }

    override fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val token = instanceIdResult.token  //토큰 생성
            SharedPreferenceManager.setString(mContext, "PREFTOKEN", token)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)  //알림허용
    }

    override fun onBackPressed() {} //뒤로가기 막기

    override fun onStop() {
        super.onStop()
        presenter.releaseView() // presenter 연결 해제
    }
}
