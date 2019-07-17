package com.nuntteuniachim.sroomi.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nuntteuniachim.sroomi.model.Data
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import com.nuntteuniachim.sroomi.R
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
            itemView.tv_recyclerview_title.text = data.title
            itemView.tv_recyclerview_content.text = data.content
        }
    }
}
