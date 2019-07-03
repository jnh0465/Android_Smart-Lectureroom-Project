package com.jiwoolee.android_smartlectureroom.view.login

import android.widget.TextView

import com.jiwoolee.android_smartlectureroom.base.BaseContract

interface LoginContract {

    interface View : BaseContract.View {
        fun autoLogin()

        fun startActivity()
    }

    interface Presenter : BaseContract.Presenter<View> {
        override fun setView(view: View?)

        override fun releaseView()

        fun loginUser(studentId: String, studentPw: String)

        fun findPassword(studentId: String, studentPw: String)

        fun validateForm(studentId: TextView?, studentPw: TextView?): Boolean
    }
}
