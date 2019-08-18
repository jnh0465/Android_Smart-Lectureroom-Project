package com.nuntteuniachim.sroomi.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.base.AttendDetailRecyclerviewAdapter
import com.nuntteuniachim.sroomi.base.SharedPreferenceManager
import com.nuntteuniachim.sroomi.retrofit.Data
import kotlinx.android.synthetic.main.activity_attenddetail.*
import java.text.SimpleDateFormat
import java.util.*

class AttendDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var classList: ArrayList<String> = ArrayList() //ArrayList 정의
    private var attendList: ArrayList<String> = ArrayList()
    private var nameList: ArrayList<String> = ArrayList()
    private var dayList: ArrayList<String> = ArrayList()
    private var idList: ArrayList<String> = ArrayList()

    private var adapter: AttendDetailRecyclerviewAdapter? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attenddetail)
        mContext = this

        val recyclerView = findViewById<View>(R.id.rec_attenddetail) as RecyclerView
        setRecyclerview(recyclerView) //recyclerview apdater 연결

        val btnToBack = findViewById<View>(R.id.btn_toback) as ImageButton
        btnToBack.setOnClickListener(this) //리스너


        val intent = intent
        val bundle = intent.extras
        val subjectId = bundle!!.getString("subjectId")
        val subjectname = bundle!!.getString("subjectname")
        textView2.text = subjectname

        SharedPreferenceManager.setString(AttendDetailActivity.mContext, "subjectId", subjectId.toString())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_toback -> finish()
        }
    }

    //Recyclerview 설정
    private fun setRecyclerview(recyclerview: RecyclerView) {
        initRecyclerview(recyclerview)
        refineAttenddata() //출석 로그 데이터 정제
        adapter!!.notifyDataSetChanged()
    }

    private fun initRecyclerview(recyclerview: RecyclerView) {
        val linearLayoutManager = LinearLayoutManager(FragmentActivity.mContext)
        recyclerview.layoutManager = linearLayoutManager

        adapter = AttendDetailRecyclerviewAdapter()
        recyclerview.adapter = adapter
    }

    //출석 로그 데이터 정제
    @SuppressLint("SimpleDateFormat")
    private fun refineAttenddata() {
        var str: String = SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFAT").toString() //node서버에서 받아온 값 저장
        str = str.replace("\\\",\\\"", "")

        val array = str.split("a")

        for (x in 1 until array.size) {
            val arrayDetail = array[x].replace("-", "/").split(",")
            for (y in 0 until arrayDetail.size - 1 step 6) idList.add(arrayDetail[y])        //과목코드

            for (y in 1 until arrayDetail.size step 6) classList.add(arrayDetail[y])        //교시
            for (y in 2 until arrayDetail.size step 6) attendList.add(arrayDetail[y])       //출결
            for (y in 3 until arrayDetail.size step 6) nameList.add(arrayDetail[y])         //과목명
            for (y in 4 until arrayDetail.size step 4) {         //날짜
                var day = ""

                val dateFormat = SimpleDateFormat("yyyy/MM/dd")
                val nDate = dateFormat.parse(arrayDetail[y])

                val cal = Calendar.getInstance()
                cal.time = nDate

                when (cal.get(Calendar.DAY_OF_WEEK)) {
                    1 -> day = "일"
                    2 -> day = "월"
                    3 -> day = "화"
                    4 -> day = "수"
                    5 -> day = "목"
                    6 -> day = "금"
                    7 -> day = "토"
                }

                dayList.add(arrayDetail[y].substring(5, 10) + "(" + day + ") " + arrayDetail[y].substring(11, 16))
            }
        }

        for (i in classList.indices) {
            val data = Data()

            android.os.Handler().postDelayed({
                if (idList[i] == SharedPreferenceManager.getString(AttendDetailActivity.mContext, "subjectId")) {
                    data.subjectDay = dayList[i].substring(0, 9)
                    data.subjectName = nameList[i]
                    data.subjectTime = classList[i] + "차시 "
                    data.subjectId = idList[i]

                    when {
                        attendList[i] == "A001" -> attendList[i] = "출석"
                        attendList[i] == "A002" -> attendList[i] = "지각"
                        attendList[i] == "A003" -> attendList[i] = "결석"
                    }
                    data.subjectAttend = attendList[i]
                    data.subjectMinute = " (" + dayList[i].substring(9, 14) + ")"

                    adapter!!.addItem(data)
                }
                adapter!!.notifyDataSetChanged()

            }, 300)
        }
    }
}