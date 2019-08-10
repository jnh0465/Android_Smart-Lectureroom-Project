package com.nuntteuniachim.sroomi.base

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nuntteuniachim.sroomi.retrofit.Data
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import com.nuntteuniachim.sroomi.R
import kotlinx.android.synthetic.main.recyclerview_item.view.*

//HomeFragment 리사이클러뷰(로그)

class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder>() {
    private val listData = ArrayList<Data>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recyclerview_item, viewGroup, false)
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
            itemView.tv_rec_name.text = data.subjectName
            itemView.tv_rec_time.text = data.subjectTime
            itemView.tv_rec_attend.text = data.subjectAttend
            when {
                data.subjectAttend=="출석" -> itemView.tv_rec_attend.setTextColor(Color.GREEN)
                data.subjectAttend=="지각" -> itemView.tv_rec_attend.setTextColor(Color.YELLOW)
                data.subjectAttend=="결석" -> itemView.tv_rec_attend.setTextColor(Color.RED)
            }
        }
    }
}
