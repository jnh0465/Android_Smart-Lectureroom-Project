package com.nuntteuniachim.sroomi.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.base.RecyclerviewAdapter
import com.nuntteuniachim.sroomi.base.SharedPreferenceManager
import com.nuntteuniachim.sroomi.retrofit.Data
import com.nuntteuniachim.sroomi.retrofit.IMyService
import com.nuntteuniachim.sroomi.retrofit.RetrofitClient
import com.nuntteuniachim.sroomi.view.SettingActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import java.util.*
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat


class HomeFragment : Fragment(), View.OnClickListener {
    private var disposable: CompositeDisposable? = CompositeDisposable()  //retrofit 통신
    private var retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)

    private var classList: ArrayList<String> = ArrayList() //ArrayList 정의
    private var attendList: ArrayList<String> = ArrayList()
    private var nameList: ArrayList<String> = ArrayList()
    private var dayList: ArrayList<String> = ArrayList()

    private var adapter: RecyclerviewAdapter? = null

    override fun onResume() {
        super.onResume()
        getAttendStateProcess() //출석 로그 가져오기
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<View>(R.id.rec_home) as RecyclerView
        setRecyclerview(recyclerView) //recyclerview apdater 연결

        val btnSetting = view.findViewById<View>(R.id.btn_setting) as ImageButton
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

    //출석 로그 가져오기
    private fun getAttendStateProcess() {
        disposable!!.add(iMyService!!.getAttendStateProcess(SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFID")!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    SharedPreferenceManager.setString(FragmentActivity.mContext, "PREFAS", response)
                }
        )
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

        adapter = RecyclerviewAdapter()
        recyclerview.adapter = adapter
    }

    //출석 로그 데이터 정제
    @SuppressLint("SimpleDateFormat")
    private fun refineAttenddata() {
        var str: String = SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFAS").toString() //node서버에서 받아온 값 저장
        str = str.replace("\\\",\\\"", "")

        val array = str.split("a")

        for (x in 1 until array.size) {
            val arrayDetail = array[x].replace("-", "/").split(",")
            for (y in 1 until arrayDetail.size step 6) classList.add(arrayDetail[y])        //교시
            for (y in 2 until arrayDetail.size step 6) attendList.add(arrayDetail[y])       //출결
            for (y in 3 until arrayDetail.size step 6) nameList.add(arrayDetail[y])         //과목명
            for (y in 4 until arrayDetail.size step 4) {         //날짜
                var day = ""

                val dateFormat = SimpleDateFormat("yyyy/MM/dd")
                val nDate = dateFormat.parse(arrayDetail[y])

                val cal = Calendar.getInstance()
                cal.time = nDate

                val dayNum = cal.get(Calendar.DAY_OF_WEEK)

                when (dayNum) {
                    1 -> day = "일"
                    2 -> day = "월"
                    3 -> day = "화"
                    4 -> day = "수"
                    5 -> day = "목"
                    6 -> day = "금"
                    7 -> day = "토"
                }

                dayList.add(arrayDetail[y] + "(" + day + ")")
            }
        }

        for (i in classList.indices) {
            val data = Data()
            data.subjectName = nameList[i]

            when {
                attendList[i] == "A001" -> attendList[i] = "출석"
                attendList[i] == "A002" -> attendList[i] = "지각"
                attendList[i] == "A003" -> attendList[i] = "결석"
            }
            data.subjectTime = dayList[i] + " " + classList[i] + "교시 "
            data.subjectAttend = attendList[i]

//            if (adapter?.itemCount!! <= 2) { //3개까지만 로그표시
            adapter!!.addItem(data)
//            }
        }
    }
}
