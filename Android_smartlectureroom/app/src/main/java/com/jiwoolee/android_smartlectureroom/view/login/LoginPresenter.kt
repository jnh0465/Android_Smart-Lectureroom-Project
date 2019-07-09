package com.jiwoolee.android_smartlectureroom.view.login

import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.model.IMyService
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

open class LoginPresenter internal constructor() : LoginContract.Presenter {
    private var view: LoginContract.View? = null

    private var disposable: CompositeDisposable? = CompositeDisposable()
    private var retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)

    override fun setView(view: LoginContract.View?) {
        this.view = view
        view?.autoLogin()
    }

    override fun releaseView() {
        disposable?.clear()
    }

    override fun findPassword(studentId: String, studentPw: String) {
        disposable!!.add(iMyService!!.changePasswordUser(studentId, studentPw)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    when (response) {
                        "1" -> Toast.makeText(LoginActivity.mContext, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        "2" -> Toast.makeText(LoginActivity.mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        )
    }

    override fun validateForm(studentId: TextView?, studentPw: TextView?): Boolean {
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

    override fun loginUser(studentId: String, studentPw: String) {
        disposable!!.add(iMyService!!.loginUser(studentId, studentPw!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    val str:String = response

                    if (response == "2") Toast.makeText(LoginActivity.mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    else if (response == "0") Toast.makeText(LoginActivity.mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                    else {
                        view?.startActivity()
                        SharedPreferenceManager.setString(LoginActivity.mContext, "PREFID", studentId)
                        SharedPreferenceManager.setString(LoginActivity.mContext, "PREFPW", studentPw)
                        SharedPreferenceManager.setString(LoginActivity.mContext, "PREFNM", response)
                    }
                }
        )
    }
}
