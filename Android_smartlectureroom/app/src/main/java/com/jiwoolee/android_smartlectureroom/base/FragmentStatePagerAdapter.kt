package com.jiwoolee.android_smartlectureroom.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.jiwoolee.android_smartlectureroom.view.main.ThirdFragment
import com.jiwoolee.android_smartlectureroom.view.main.HomeFragment
import com.jiwoolee.android_smartlectureroom.view.main.ScheduleFragment

class FragmentStatePagerAdapter(var fragNum : Int, fm : FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> HomeFragment()
            1 -> ScheduleFragment()
            2 -> ThirdFragment()
            else -> null
        }
    }

    override fun getCount(): Int = fragNum

    override fun getItemPosition(`object`: Any): Int {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return PagerAdapter.POSITION_NONE
    }

}