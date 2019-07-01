package com.jiwoolee.android_smartlectureroom.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.jiwoolee.android_smartlectureroom.model.Data
import com.jiwoolee.android_smartlectureroom.R

import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView

class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder>() {
    private val listData = ArrayList<Data>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_main, viewGroup, false)
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
        private val textView1: TextView = itemView.findViewById(R.id.textView1)
        private val textView2: TextView = itemView.findViewById(R.id.textView2)

        internal fun onBind(data: Data) {
            textView1.text = data.title
            textView2.text = data.content
        }
    }
}
