package com.jiwoolee.android_smartlectureroom.view.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.base.BaseActivity
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity(), LoginContract.View, View.OnClickListener {
    private val presenter = LoginPresenter() 

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mContext = this

        presenter.setView(this) // presenter 연결

        btn_login.setOnClickListener(this) //리스너 연결
        tv_findpw.setOnClickListener(this)
        chk_autologin.setOnCheckedChangeListener(onCheckedChangeListener)

        chk_autologin!!.isChecked = SharedPreferenceManager.getBoolean(mContext, "PREFCB") //checkbox 상태 저장
    }

    //listener//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onClick(v: View) { //버튼클릭시
        when (v.id) {
            R.id.btn_login -> {
                val test = presenter.validateForm(tv_editid, tv_editpw) //폼 채움 여부 확인
                if (test)  presenter.loginUser(tv_editid.text.toString(), tv_editpw.text.toString()) //로그인
            }
//            R.id.tv_findpw -> materialDialog() //비밀번호 찾기
        }
    }

    private var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        when {
            isChecked -> SharedPreferenceManager.setBoolean(mContext, "PREFCB", true) //초기 상태 저장
            else -> SharedPreferenceManager.setBoolean(mContext, "PREFCB", false)
        }
    }

    override fun autoLogin() {
        val studentId:String = SharedPreferenceManager.getString(mContext, "PREFID").toString() //SharedPreference
        val studentPw:String = SharedPreferenceManager.getString(mContext, "PREFPW").toString()
        val checkboxState:Boolean = SharedPreferenceManager.getBoolean(mContext, "PREFCB")
        if (studentId.isNotEmpty() && checkboxState) { //자동로그인시
            showProgressDialog() //프로그래스바 보이기
            presenter.loginUser(studentId, studentPw) //로그인
        }
    }
//
//    private fun materialDialog() {
//        val registerLayout = LayoutInflater.from(mContext).inflate(R.layout.findpw_layout, null)
//        MaterialStyledDialog.Builder(mContext)
//                .setTitle("FIND PASSWORD")
//                .setCustomView(registerLayout)
//                .setNegativeText("CANSEL")
//                .onNegative { dialog, which -> dialog.dismiss() }
//                .setPositiveText("SEND")
//                .onPositive { dialog, which ->
//                    val test = presenter.validateForm(edit_id, edit_password) //폼 채움 여부 확인
//                    if (test && edit_password === edit_password2) presenter.findPassword(edit_id.text.toString(), edit_password.text.toString())
//                }.show()
//    }

    override fun startActivity() {
        val intent = Intent(mContext, FragmentActivity::class.java)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        presenter.releaseView() // presenter 연결 해제
    }
}
