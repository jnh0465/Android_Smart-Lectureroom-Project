package com.jiwoolee.android_smartlectureroom.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jiwoolee.android_smartlectureroom.model.Data
import com.jiwoolee.android_smartlectureroom.R
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_main.view.*

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
        internal fun onBind(data: Data) {
            itemView.textView1.text = data.title
            itemView.textView2.text = data.content
        }
    }
}
