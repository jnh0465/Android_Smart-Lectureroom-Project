package com.jiwoolee.android_smartlectureroom.view.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.base.BaseActivity
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.model.IMyService
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

class LoginActivity : BaseActivity(), View.OnClickListener {
    private var disposable: CompositeDisposable? = null
    private var iMyService: IMyService? = null

    private var txtloginId: TextView? = null
    private var txtloginPw: TextView? = null
    private var txtFind: TextView? = null
    private var btnLogin: Button? = null
    private var ckAutologin: CheckBox? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    private var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked) { //체크시
            SharedPreferenceManager.setBoolean(mContext, "PREFCB", true)
        } else {
            SharedPreferenceManager.setBoolean(mContext, "PREFCB", false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mContext = this

        disposable = CompositeDisposable()
        val retrofitClient = RetrofitClient.getInstance()
        iMyService = (retrofitClient as Retrofit).create(IMyService::class.java)

        txtloginId = findViewById(R.id.edit_id) //
        txtloginPw = findViewById(R.id.edit_password)
        btnLogin = findViewById(R.id.btn_login)
        txtFind = findViewById(R.id.text_findpw)
        ckAutologin = findViewById(R.id.autologin_checkBox)

        btnLogin!!.setOnClickListener(this) //리스너 연결
        txtFind!!.setOnClickListener(this)
        ckAutologin!!.setOnCheckedChangeListener(onCheckedChangeListener)

        val checkboxState = SharedPreferenceManager.getBoolean(mContext, "PREFCB") //SharedPreference
        ckAutologin!!.isChecked = checkboxState

        autoLogin();
    }

    //listener//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onClick(v: View) { //버튼클릭시
        val i = v.id
        if (i == R.id.btn_login) {
            val test = validateForm(txtloginId, txtloginPw) //폼 채움 여부 확인
            if (test) {
                loginUser(txtloginId!!.text.toString(), txtloginPw!!.text.toString())
            }
        } else if (i == R.id.text_findpw) {
            //            MaterialDialog(presenter);
        }
    }

    private fun autoLogin() {
        val studentId = SharedPreferenceManager.getString(mContext, "PREFID") //SharedPreference
        val studentPw = SharedPreferenceManager.getString(mContext, "PREFPW")
        val checkboxState = SharedPreferenceManager.getBoolean(mContext, "PREFCB")
        if (studentId!!.isNotEmpty() && checkboxState) { //자동로그인시
            showProgressDialog() //프로그래스바 보이기
            loginUser(studentId, studentPw)
        }
    }

    private fun loginUser(studentId: String, studentPw: String?) {
        disposable!!.add(iMyService!!.loginUser(studentId, studentPw!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    when (response) {
                        "1" -> { //로그인 성공시
                            val intent = Intent(mContext, FragmentActivity::class.java)
                            startActivity(intent)
                            SharedPreferenceManager.setString(mContext, "PREFID", studentId)
                            SharedPreferenceManager.setString(mContext, "PREFPW", studentPw)
                        }
                        "2" -> Toast.makeText(mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                        "0" -> Toast.makeText(mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        )
    }

    private fun findPassword(studentId: String, studentPw: String) {
        disposable!!.add(iMyService!!.changePasswordUser(studentId, studentPw)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    when (response) {
                        "1" -> Toast.makeText(mContext, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        "2" -> Toast.makeText(mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        )
    }

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
}
