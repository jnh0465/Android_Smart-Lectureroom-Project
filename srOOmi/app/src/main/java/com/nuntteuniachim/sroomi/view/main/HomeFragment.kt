package com.nuntteuniachim.sroomi.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.view.setting.SettingActivity

class HomeFragment : Fragment(), FragmentContract.View, View.OnClickListener {
    private val presenter = FragmentPresenter()

    override fun onResume() {
        super.onResume()
        presenter.getAttendStateProcess()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        presenter.setView(this) //presenter 연결

        val recyclerView = view.findViewById<View>(R.id.recyclerview) as RecyclerView
        presenter.setRecyclerview(recyclerView) //recyclerview apdater 연결

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

    override fun getToken() {}
}
