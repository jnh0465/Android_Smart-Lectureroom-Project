package com.nuntteuniachim.sroomi.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.nuntteuniachim.sroomi.retrofit.Data
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import com.nuntteuniachim.sroomi.R
import kotlinx.android.synthetic.main.recyclerview_attend_item.view.*
import kotlinx.android.synthetic.main.recyclerview_attenddetail_item.view.*

//AttendDetailFragment 리사이클러뷰
//AttendFragment의 리사이클러뷰 아이템 클릭시 넘어가는 attendDetailActivity에 적용되는 리스트뷰

class AttendDetailRecyclerviewAdapter : RecyclerView.Adapter<AttendDetailRecyclerviewAdapter.MyViewHolder>(){
    private val listData = ArrayList<Data>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recyclerview_attenddetail_item, viewGroup, false)
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
            itemView.tv_attend_recdname.text = data.subjectName
            itemView.tv_attend_recdtime.text = data.subjectTime
            itemView.tv_attend_recdattend.text = data.subjectAttend

            when {
                data.subjectAttend == "출석" -> itemView.tv_attend_recdattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.attend))
                data.subjectAttend == "지각" -> itemView.tv_attend_recdattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.late))
                data.subjectAttend == "결석" -> itemView.tv_attend_recdattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.absence))
            }
            itemView.tv_attend_recdday.text = data.subjectDay
            itemView.tv_attend_recdminute.text = data.subjectMinute
        }
    }
}