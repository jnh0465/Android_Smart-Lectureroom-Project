package com.jiwoolee.android_smartlectureroom.base

import com.jiwoolee.android_smartlectureroom.view.login.LoginContract

class BaseContract {

    interface Presenter<T> {
        fun setView(view: LoginContract.View?)
        fun releaseView()
    }

    interface View
}
