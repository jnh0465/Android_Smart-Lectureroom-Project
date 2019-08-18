package com.nuntteuniachim.sroomi.base

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.nuntteuniachim.sroomi.retrofit.Data
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import com.nuntteuniachim.sroomi.R
import kotlinx.android.synthetic.main.recyclerview_home_item.view.*

//HomeFragment 리사이클러뷰(로그)

class HomeRecyclerviewAdapter : RecyclerView.Adapter<HomeRecyclerviewAdapter.MyViewHolder>() {
    private val listData = ArrayList<Data>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recyclerview_home_item, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(listData[position])
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun addItem(data: Data) {
        listData.add(data)
    }

    class MyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun onBind(data: Data) {
            itemView.tv_home_rechname.text = data.subjectName
            itemView.tv_home_rechtime.text = data.subjectTime
            itemView.tv_home_rechattend.text = data.subjectAttend
            when {
                data.subjectAttend == "출석" -> itemView.tv_home_rechattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.attend))
                data.subjectAttend == "지각" -> itemView.tv_home_rechattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.late))
                data.subjectAttend == "결석" -> itemView.tv_home_rechattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.absence))
            }
            itemView.tv_home_rechday.text = data.subjectDay
        }
    }
}
