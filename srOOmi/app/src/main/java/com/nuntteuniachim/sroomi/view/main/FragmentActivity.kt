package com.nuntteuniachim.sroomi.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.nuntteuniachim.sroomi.base.SharedPreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.base.FragmentStatePagerAdapter
import com.nuntteuniachim.sroomi.retrofit.IMyService
import com.nuntteuniachim.sroomi.retrofit.RetrofitClient
import com.nuntteuniachim.sroomi.view.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_fragment.*
import retrofit2.Retrofit
import android.R.menu
import android.content.Intent
import android.view.MenuInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.nuntteuniachim.sroomi.view.SettingActivity

class FragmentActivity : AppCompatActivity(), View.OnClickListener {
    private var disposable: CompositeDisposable? = CompositeDisposable() //retrofit 통신
    private var retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)

    private val fragmentAdapter: FragmentStatePagerAdapter by lazy { FragmentStatePagerAdapter(3, supportFragmentManager) } //프래그먼트 어댑터

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        mContext = this

        val viewpager = findViewById<View>(R.id.fragment_container) as ViewPager
        initSetting(fragmentAdapter, viewpager) //최초 실행시

        btn_homefragment.setOnClickListener(this) //리스너 연결
        btn_schedulefragment.setOnClickListener(this)
        btn_attendfragment.setOnClickListener(this)

        tv_home_studentname.text = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFNM").toString().substring(1, 4)+"님"

        val toolbar = findViewById<View>(R.id.up_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_icon -> {
                val intent = Intent(mContext, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_homefragment -> fragment_container.currentItem = 0
            R.id.btn_schedulefragment -> fragment_container.currentItem = 1
            R.id.btn_attendfragment -> fragment_container.currentItem = 2
        }
    }

    fun toAttend() {
        fragment_container.currentItem = 2
    }

    fun toMain() {
        fragment_container.currentItem = 0
    }

    private fun initSetting(fragmentAdapter: FragmentStatePagerAdapter, viewpager: ViewPager) {
        initViewpagerAdapter(fragmentAdapter, viewpager) //뷰페이저 세팅

        val isFirst = SharedPreferenceManager.getToken(LoginActivity.mContext, "PREFFIRST")  //최초 실행시
        if (!isFirst) {
            updateToken() //토큰 업로드
            Handler().postDelayed({
                viewpager.adapter!!.notifyDataSetChanged() //새로고침
            }, 300)
        }
    }

    //뷰페이저 세팅
    private fun initViewpagerAdapter(fragmentAdapter: FragmentStatePagerAdapter, viewpager: ViewPager) {
        viewpager.adapter = fragmentAdapter //adapter 연결
        viewpager.offscreenPageLimit = 3    //pageLimit 지정
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                viewpager.adapter!!.notifyDataSetChanged() //새로고침
            }
        })
    }

    //토큰 업로드
    private fun updateToken() {
        android.os.Handler().postDelayed({
            disposable!!.add(iMyService!!.sendToken(SharedPreferenceManager.getString(FragmentActivity.mContext, "PREFID")!!, SharedPreferenceManager.getString(LoginActivity.mContext, "PREFTOKEN")!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { response ->
                        //Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show(); //node 서버에서 response.json으로 보낸 응답 받아서 toast
                        when (response) {
                            "1" -> //로그인 성공시
                                Toast.makeText(FragmentActivity.mContext, "토큰이 등록되었습니다" + SharedPreferenceManager.getString(LoginActivity.mContext, "PREFTOKEN")!!, Toast.LENGTH_SHORT).show()
                            "2" -> Toast.makeText(FragmentActivity.mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
                            "0" -> Toast.makeText(FragmentActivity.mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            )
            SharedPreferenceManager.setToken(LoginActivity.mContext, "PREFFIRST", true)
        }, 3000)
    }

    //뒤로가기 버튼을 두번 연속으로 눌러야 종료
    private var time: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis()
            Toast.makeText(applicationContext, "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        disposable?.clear()
    }
}

