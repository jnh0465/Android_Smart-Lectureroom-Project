package com.jiwoolee.android_smartlectureroom.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.jiwoolee.android_smartlectureroom.view.login.LoginActivity
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity.Companion.mContext

class HomeFragment : Fragment(), View.OnClickListener {
    internal var viewPager: ViewPager? = null
    private var adapter: RecyclerviewAdapter? = null
    private val idList = ArrayList<String>()
    private val lectureList = ArrayList<String>()
    private var jsonArray: JSONArray? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnLogout = view.findViewById<View>(R.id.btn_logout) as ImageButton
        val recyclerView = view.findViewById<View>(R.id.recyclerview) as RecyclerView
        setRecyclerview(recyclerView)
        btnLogout.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> {
                val intent = Intent(LoginActivity.mContext, LoginActivity::class.java)
                startActivity(intent)
                SharedPreferenceManager.clear(mContext)
            }
        }
    }

    private fun setRecyclerview(recyclerview: RecyclerView) { /////////수정 및 분리 예정
        val linearLayoutManager = LinearLayoutManager(mContext)
        recyclerview.layoutManager = linearLayoutManager

        adapter = RecyclerviewAdapter()
        recyclerview.adapter = adapter

        val str = "[{\"_id\":\"5d07b6b7f9a9919a2e20f664\",\"student_id\":\"2016100912\",\"student_password\":\"10\",\"student_name\":\"이지우\"," +
                "\"student_picture\":\"test_path...\",\"lecture_list\":[\"1\",\"2\",\"3\"],\"student_token\":\"" +
                "d6zd8syZ-0A:APA91bHnTLPvZqvO7CWKychzv0kuWf89IOBStWLBvMIpfijQ_8alJkY4lxfn7U8rUd-YDJAu0hYuzaWspPy0Hx6KACLUUnEg4Slg7AHzNeqNiqcscnli3uy_v7BGmdzndy2NPB_3WJPx\"}]"
        try {
            jsonArray = JSONArray(str)
            for (i in 0 until jsonArray!!.length()) {
                val jsonObject = jsonArray!!.getJSONObject(i)
                val id = jsonObject.getString("student_id")
                val lecture_list = jsonObject.getString("lecture_list")
                idList.add(id)
                lectureList.add(lecture_list)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        //        List<String> listTitle = idList;
        //        List<String> listContent = lectureList;
        val listTitle = listOf("알고리즘3", "알고리즘2", "알고리즘1", "자바스크립트2", "자바스크립트1")
        val listContent = listOf("출석", "출석", "지각", "출석", "출석")

        for (i in listTitle.indices) {
            val data = Data()
            data.title = listTitle[i]
            data.content = listContent[i]
            adapter!!.addItem(data)
        }
        adapter!!.notifyDataSetChanged()
    }

}
