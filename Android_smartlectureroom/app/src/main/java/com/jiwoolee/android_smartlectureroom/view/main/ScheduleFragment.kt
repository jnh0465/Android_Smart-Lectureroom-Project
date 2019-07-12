package com.jiwoolee.android_smartlectureroom.view.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import java.util.ArrayList
import androidx.fragment.app.Fragment
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity.Companion.mContext

class ScheduleFragment : Fragment(), FragmentContract.View {
    private val presenter = FragmentPresenter()

    private var dayList: ArrayList<String> = ArrayList() //ArrayList 정의
    private var timeList: ArrayList<String> = ArrayList()
    private var idList: ArrayList<String> = ArrayList()
    private var lectureList: ArrayList<String> = ArrayList()
    private var lectureroomList: ArrayList<String> = ArrayList()
    private var scheduleList: ArrayList<String> = ArrayList()

    private var tv = arrayOfNulls<TextView>(100)
    private val color: Array<Int> = arrayOf(Color.rgb(230, 230, 250), Color.rgb(255, 228, 225), Color.rgb(240, 255, 255), Color.rgb(240, 255, 240), Color.rgb(255, 250, 205))

    override fun onResume() {
        super.onResume()
        presenter.getSchedule()
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        presenter.setView(this) //presenter 연결

        refineScheduledata()
        initKeyvalue()

        // 현재 인덱스의 요일과 다음 인덱스의 요일이 같고, 현재 인덱스의 시간+1이 다음 인덱스의 시간과 같으면 연강으로 인식하도록
        // 코드를 작성하려 했으나 if(scheduleList[i+1] 이 작동하지 않아 SharedPreferenceManager사용

        for (i in 0 until dayList.size) {
            var day: Int = SharedPreferenceManager.getInt(mContext, "PREFDAY") // 이전 인덱스의 값 가져오기
            var time: Int = SharedPreferenceManager.getInt(mContext, "PREFTIME")

            scheduleList.add(dayList[i] + timeList[i])

            tv[i] = view.findViewById<View>(getId(scheduleList[i])) as TextView?  //textview id 받아오기
            tv[i]?.setBackgroundColor(color[idList[i].toInt()])  //배경 설정
            tv[i]?.text = lectureList[i] + "\n" + lectureroomList[i] //강의명, 강의실 표시

            //현재 인덱스의 요일과 다음 인덱스의 요일이 같고, 현재 인덱스의 시간+1이 다음 인덱스의 시간과 같으면 연강으로 인식
            if (day == dayList[i].toInt() && time == timeList[i].toInt()) {
                tv[i]?.text = "" //연강 중 첫수업만 강의명, 강의실 표시하고 나머지는 비워놓기
            }

            SharedPreferenceManager.setInt(mContext, "PREFDAY", dayList[i].toInt())   //현재 인덱스의 값 저장
            SharedPreferenceManager.setInt(mContext, "PREFTIME", timeList[i].toInt() + 1)
        }

        return view
    }

    private fun refineScheduledata(){
        val str: String = SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFSC").toString() //node서버에서 받아온 값 저장
        val array = str.split("a")

        for (x in 1 until array.size step 2) {
            val arrayDetail = array[x].split(",")

            for (y in 0 until arrayDetail.size step 5) dayList.add(arrayDetail[y])         //요일
            for (y in 1 until arrayDetail.size step 5) timeList.add(arrayDetail[y])        //교시
            for (y in 2 until arrayDetail.size step 5) lectureList.add(arrayDetail[y])     //강의
            for (y in 3 until arrayDetail.size step 5) lectureroomList.add(arrayDetail[y]) //강의실
            for (y in 4 until arrayDetail.size step 5) idList.add(arrayDetail[y])          //id값
        }
    }

    private fun initKeyvalue(){
        SharedPreferenceManager.removeKey(mContext, "PREFDAY") //키값 초기화
        SharedPreferenceManager.removeKey(mContext, "PREFTIME")
    }


    private fun drawSchedule(){

    }

    private fun getId(id: String): Int { //textview의 id받아오기
        return resources.getIdentifier("text_$id", "id", "com.jiwoolee.android_smartlectureroom")
    }

    override fun getToken() {}
}