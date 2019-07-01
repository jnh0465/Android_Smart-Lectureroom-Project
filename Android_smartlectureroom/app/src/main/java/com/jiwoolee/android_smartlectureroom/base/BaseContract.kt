package com.jiwoolee.android_smartlectureroom.base

class BaseContract {

    interface Presenter<T> {
        fun setView(view: T)
        fun releaseView()
    }

    interface View
}
