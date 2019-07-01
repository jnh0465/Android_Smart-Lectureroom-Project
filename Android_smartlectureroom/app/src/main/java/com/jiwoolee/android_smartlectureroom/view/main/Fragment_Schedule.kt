package com.jiwoolee.android_smartlectureroom.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView

import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity

import java.util.ArrayList

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

class Fragment_Schedule : Fragment() {
    internal var viewPager: ViewPager? = null

    private var dataList: ArrayList<String>? = null
    private var dayList: ArrayList<String>? = null
    private var timeList: ArrayList<String>? = null
    private var timeList22222: ArrayList<String>? = null

    private var textView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        dataList = ArrayList()
        dayList = ArrayList()
        timeList = ArrayList()
        timeList22222 = ArrayList()

        (activity as FragmentActivity).getSchedule()

        val b = getId(12)
        textView = view.findViewById<View>(b) as TextView

        //        btn.setBackgroundColor(11111111);

        android.os.Handler().postDelayed(
                {
                    //                        RecyclerView recyclerView = findViewById(R.id.recyclerview2);
                    //                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.mContext);
                    //                        recyclerView.setLayoutManager(linearLayoutManager);
                    //
                    //                        adapter = new RecyclerviewAdapter();
                    //                        recyclerView.setAdapter(adapter);

                    val str = SharedPreferenceManager.getString(FragmentActivity.mContext, "PREF_SC")
                    val array = str!!.split("a".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    var i = 1
                    while (i < array.size) {
                        dataList!!.add(array[i])
                        val arrayDetail = array[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var j = 0
                        while (j < arrayDetail.size) {
                            dayList!!.add(arrayDetail[j])
                            j += 4
                        }
                        var k = 1
                        while (k < arrayDetail.size) {
                            timeList!!.add(arrayDetail[k])
                            k += 4
                        }
                        i += 2
                    }

                    //                        for (int i = 0; i < dataList.size(); i++) {
                    //                            timeList22222.add("btn_"+dayList.get(i)+timeList.get(i));
                    //                            String a = "R.id.btn_"+"1"+"2";
                    //                                    dayList.get(i)+timeList.get(i);
                    //
                    //                            int to = Integer.parseInt(a);
                    //
                    //                            btn=findViewById(to);
                    //
                    //                            btn.setBackgroundColor(getResources().getColor(R.color.md_divider_black));
                    //                            Data data = new Data();
                    //                            data.setTitle(dayList.get(i)+timeList.get(i));
                    //                            data.setContent(timeList.get(i));
                    //                            adapter.addItem(data);
                },
                //                        adapter.notifyDataSetChanged();
                //                    }
                3000)

        return view
    }

    override fun onResume() {
        super.onResume()

        textView!!.text = "안녀엉"
        textView!!.layoutParams = TableRow.LayoutParams(3) //layout_column
    }

    private fun getId(id: Int): Int {
        return resources.getIdentifier("text_$id", "id", "com.jiwoolee.android_smartlectureroom")
    }
}