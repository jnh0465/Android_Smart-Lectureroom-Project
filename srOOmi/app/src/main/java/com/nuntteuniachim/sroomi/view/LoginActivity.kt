package com.nuntteuniachim.sroomi.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.base.BaseActivity
import com.nuntteuniachim.sroomi.base.SharedPreferenceManager
import com.nuntteuniachim.sroomi.retrofit.IMyService
import com.nuntteuniachim.sroomi.retrofit.RetrofitClient
import com.nuntteuniachim.sroomi.view.main.FragmentActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit

//로그인

class LoginActivity : BaseActivity(), View.OnClickListener {
    private var disposable: CompositeDisposable? = CompositeDisposable()  //retrofit 통신
    private var retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)

    private val TOPIC = "send" //fcm firebase 토픽 선언

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mContext = this

        autoLogin() //자동로그인

        val isFirst = SharedPreferenceManager.getToken(LoginActivity.mContext, "PREFFIRST")  //최초 실행시
        if (!isFirst) {
            getToken() //토큰생성
        }

        btn_login.setOnClickListener(this) //리스너 연결
        chk_autologin.setOnCheckedChangeListener(onCheckedChangeListener)

        chk_autologin!!.isChecked = SharedPreferenceManager.getBoolean(mContext, "PREFCB") //checkbox 상태 저장
    }

    //listener//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onClick(v: View) { //버튼클릭시
        when (v.id) {
            R.id.btn_login -> {
                val test = validateForm(tv_editid, tv_editpw) //폼 채움 여부 확인
                if (test) loginUser(tv_editid.text.toString(), tv_editpw.text.toString()) //로그인
            }
        }
    }

    private var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        when {
            isChecked -> SharedPreferenceManager.setBoolean(mContext, "PREFCB", true) //초기 상태 저장
            else -> SharedPreferenceManager.setBoolean(mContext, "PREFCB", false)
        }
    }


    //Token
    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val token = instanceIdResult.token  //토큰 생성
            SharedPreferenceManager.setString(LoginActivity.mContext, "PREFTOKEN", token)
            Toast.makeText(LoginActivity.mContext, "토큰이 생성되었습니다" + SharedPreferenceManager.getString(LoginActivity.mContext, "PREFTOKEN")!!, Toast.LENGTH_SHORT).show()
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)  //알림허용
    }

    //자동로그인
    private fun autoLogin() {
        val studentId: String = SharedPreferenceManager.getString(mContext, "PREFID").toString() //SharedPreference
        val studentPw: String = SharedPreferenceManager.getString(mContext, "PREFPW").toString()
        val checkboxState: Boolean = SharedPreferenceManager.getBoolean(mContext, "PREFCB")
        if (studentId.isNotEmpty() && checkboxState) { //자동로그인시
            showProgressDialog() //프로그래스바 보이기
            loginUser(studentId, studentPw) //로그인
        }
    }

    //폼 채움 여부 확인
    private fun validateForm(studentId: TextView?, studentPw: TextView?): Boolean {
        var valid = true
        val id = studentId!!.text.toString()
        val pw = studentPw!!.text.toString()

        when {
            TextUtils.isEmpty(id) -> {
                studentId.error = "학번을 입력해주세요"
                valid = false
            }
            TextUtils.isEmpty(pw) -> {
                studentId.error = null
                studentPw.error = "비밀번호를 입력해주세요"
                valid = false
            }
            else -> studentPw.error = null
        }
        return valid
    }

    //로그인
    private fun loginUser(studentId: String, studentPw: String) {
        disposable!!.add(iMyService!!.loginUser(studentId, studentPw)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    when (response) {
                        "2" -> Toast.makeText(mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                        "0" -> Toast.makeText(mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                        else -> {
                            val intent = Intent(mContext, FragmentActivity::class.java)
                            startActivity(intent)

                            SharedPreferenceManager.setString(mContext, "PREFID", studentId)
                            SharedPreferenceManager.setString(mContext, "PREFPW", studentPw)
                            SharedPreferenceManager.setString(mContext, "PREFNM", response)

                            finish()
                        }
                    }
                }
        )
    }

    override fun onStop() {
        super.onStop()
        disposable?.clear()
    }
}
