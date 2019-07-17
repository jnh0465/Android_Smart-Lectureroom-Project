package com.nuntteuniachim.sroomi.view.login

import android.widget.TextView

interface LoginContract {

    interface View {
        fun autoLogin()

        fun startActivity()
    }

    interface Presenter {
        fun setView(view: View?)

        fun releaseView()

        fun loginUser(studentId: String, studentPw: String)

        fun findPassword(studentId: String, studentPw: String)

        fun validateForm(studentId: TextView?, studentPw: TextView?): Boolean
    }
}
