package com.nuntteuniachim.sroomi.base

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nuntteuniachim.sroomi.retrofit.Data
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.view.main.AttendDetailActivity
import kotlinx.android.synthetic.main.recyclerview_attend_item.view.*
import android.os.Bundle
import androidx.core.content.ContextCompat

//AttendFragment 리사이클러뷰

class AttendRecyclerviewAdapter : RecyclerView.Adapter<AttendRecyclerviewAdapter.MyViewHolder>() {
    private val listData = ArrayList<Data>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recyclerview_attend_item, viewGroup, false)
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
            itemView.tv_attend_recname.text = data.subjectName
            itemView.tv_attend_rectime.text = data.subjectTime
            itemView.tv_attend_recattend.text = data.subjectAttend
            itemView.tv_attend_recid.text = data.subjectId

            when {
                data.subjectAttend == "출석" -> itemView.tv_attend_recattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.attend))
                data.subjectAttend == "지각" -> itemView.tv_attend_recattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.late))
                data.subjectAttend == "결석" -> itemView.tv_attend_recattend.setTextColor(ContextCompat.getColor(itemView.context!!, R.color.absence))
            }
            itemView.tv_attend_recday.text = data.subjectDay
            itemView.tv_attend_recminute.text = data.subjectMinute

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, AttendDetailActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val bundle = Bundle()
                bundle.putString("subjectId", data.subjectId)
                bundle.putString("subjectname", data.subjectName)
                intent.putExtras(bundle)

                itemView.context.startActivity(intent)
            }
        }
    }
}