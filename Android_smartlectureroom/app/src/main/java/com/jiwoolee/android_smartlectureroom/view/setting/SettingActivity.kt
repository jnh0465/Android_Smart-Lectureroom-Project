package com.jiwoolee.android_smartlectureroom.view.setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.view.login.LoginActivity
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity(), View.OnClickListener  {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        mContext = this

        tv_username.text = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFNM").toString().substring(1,4)
        tv_hakbun.text = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFID").toString().substring(2,4)+"학번"
        btn_logout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> {
                val intent = Intent(mContext, LoginActivity::class.java)
                startActivity(intent)
                SharedPreferenceManager.clear(FragmentActivity.mContext)
            }
        }
    }
}
