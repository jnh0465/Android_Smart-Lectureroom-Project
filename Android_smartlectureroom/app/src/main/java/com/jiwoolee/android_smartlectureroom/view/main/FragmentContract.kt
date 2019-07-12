package com.jiwoolee.android_smartlectureroom.view.main

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.jiwoolee.android_smartlectureroom.base.FragmentStatePagerAdapter

interface FragmentContract {

    interface View {
        fun getToken()
    }

    interface Presenter {
        fun setView(view: FragmentContract.View?)
        fun releaseView()

        //HomeFragment//////////////////////////////////////////////////////////////////////////////
        fun isFirst(fragmentAdapter: FragmentStatePagerAdapter, viewpager : ViewPager)

        fun updateToken() //Token
        fun getAttendStateProcess()

        fun initViewpagerAdapter(fragmentAdapter : FragmentStatePagerAdapter, viewpager : ViewPager) //Viewpager

        fun setRecyclerview(recyclerview: RecyclerView) //Recyclerview
        fun initRecyclerview(recyclerview: RecyclerView)
        fun refineAttenddata()

        //ScheduleFragment///////////////////////////////////////////////////////////////////////////

        fun refineScheduledata()
        fun drawSchedule()
        fun getSchedule()
    }
}
