package com.jiwoolee.android_smartlectureroom.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.base.RecyclerviewAdapter
import com.jiwoolee.android_smartlectureroom.model.Data
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.view.SettingActivity
import com.jiwoolee.android_smartlectureroom.view.login.LoginActivity
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity.Companion.mContext
import org.json.JSONObject

class HomeFragment : Fragment(), View.OnClickListener {
    private var adapter: RecyclerviewAdapter? = null

    private var classList: ArrayList<String> = ArrayList() //ArrayList 정의
    private var attendList: ArrayList<String> = ArrayList()
    private var nameList: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        (activity as FragmentActivity).getAttendStateProcess() //node서버에서 값 받아오는 함수

        val btnSetting = view.findViewById<View>(R.id.btn_setting) as ImageButton
        val recyclerView = view.findViewById<View>(R.id.recyclerview) as RecyclerView
        setRecyclerview(recyclerView)
        btnSetting.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_setting -> {
                val intent = Intent(FragmentActivity.mContext, SettingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setRecyclerview(recyclerview: RecyclerView) {
        val linearLayoutManager = LinearLayoutManager(mContext)
        recyclerview.layoutManager = linearLayoutManager

        adapter = RecyclerviewAdapter()
        recyclerview.adapter = adapter

        val str:String = SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFAS").toString() //node서버에서 받아온 값 저장
        val array = str.split("a")
        for (x in 1 until array.size step 2){
            val arrayDetail = array[x].split(",")

            for(y in 1 until arrayDetail.size step 4)  classList.add(arrayDetail[y])        //교시
            for(y in 2 until arrayDetail.size step 4)  attendList.add(arrayDetail[y])       //출결
            for(y in 3 until arrayDetail.size step 4)  nameList.add(arrayDetail[y])         //과목명
        }

        for (i in classList.indices) {
            val data = Data()
            data.title = nameList[i]
            data.content = classList[i]+"교시 "+attendList[i]
            adapter!!.addItem(data)
        }
        adapter!!.notifyDataSetChanged()
    }

}
