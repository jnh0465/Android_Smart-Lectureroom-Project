package com.nuntteuniachim.sroomi.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.base.BaseActivity
import com.nuntteuniachim.sroomi.base.SharedPreferenceManager
import com.nuntteuniachim.sroomi.fcm.MyFirebaseMessagingService
import com.nuntteuniachim.sroomi.retrofit.IMyService
import com.nuntteuniachim.sroomi.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_attendrequest.*
import org.json.JSONObject
import retrofit2.Retrofit

//푸시 클릭시 나타나는 액티비티 //요청/재요청

class AttendRequestActivity : BaseActivity(), View.OnClickListener {
    private var disposable: CompositeDisposable? = CompositeDisposable()  //retrofit 통신
    private var retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)

    private val jsonString = SharedPreferenceManager.getString(MyFirebaseMessagingService.mContext, "PREFATTEND").toString()
    private val jObject = JSONObject(jsonString)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendrequest)

        btn_request.setOnClickListener(this) //리스너 연결
        btn_rerequest.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_request -> {
                jObject.put("requestType", "attendRequest")
                textView.text = "$jObject" //확인용
                pushResponse(jObject.toString())
            }
            R.id.btn_rerequest -> {
                jObject.put("requestType", "attendReRequest")
                textView.text = "$jObject"
                pushResponse(jObject.toString())
            }
        }
    }

    private fun pushResponse(student_attendstate: String) {
        disposable!!.add(iMyService!!.pushResponse(student_attendstate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    when (response) {
                        "1" -> {
                            textView.text = "요청완료"
                        }
                    }
                }
        )
    }

    override fun onStop() {
        super.onStop()
        disposable?.clear()
    }
}
