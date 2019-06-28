package com.jiwoolee.android_smartlectureroom.view.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.base.BaseActivity;
import com.jiwoolee.android_smartlectureroom.base.MyAdapter;
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.model.Data;
import com.jiwoolee.android_smartlectureroom.view.main.MainActivity;
import com.jiwoolee.android_smartlectureroom.view.main.MainContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduleActivity extends BaseActivity {
    private SchedulePresenter presenter = new SchedulePresenter();
    public static Context mContext;
    private ScheduleContract.View view;
//    private MyAdapter adapter;

    private ArrayList<String> dataList;
    private ArrayList<String> dayList;
    private ArrayList<String> timeList;
    private ArrayList<String> timeList22222;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_main_table);
        mContext = this;
        presenter.getSchedule();

        dataList = new ArrayList<>();
        dayList = new ArrayList<>();
        timeList = new ArrayList<>();
        timeList22222 = new ArrayList<>();


        int b = getId(12);
        textView = findViewById(b);
        textView.setText("안녀엉");
        textView.setLayoutParams(new TableRow.LayoutParams(3)); //layout_column
//        btn.setBackgroundColor(11111111);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
//                        RecyclerView recyclerView = findViewById(R.id.recyclerview2);
//                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.mContext);
//                        recyclerView.setLayoutManager(linearLayoutManager);
//
//                        adapter = new MyAdapter();
//                        recyclerView.setAdapter(adapter);

                        String str = SharedPreferenceManager.getString(ScheduleActivity.mContext,"PREF_SC");
                        String[] array = str.split("a");
                        for(int i=1;i<array.length;i=i+2) {
                            dataList.add(array[i]);
                            String[] array_detail = array[i].split(",");
                            for(int j=0; j<array_detail.length; j=j+4){
                                dayList.add(array_detail[j]);
                            }
                            for(int k=1; k<array_detail.length; k=k+4){
                                timeList.add(array_detail[k]);
                            }
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
                        }
//                        adapter.notifyDataSetChanged();
//                    }
                },
                3000);
    }

    private int getId(int id){
        int resID = getResources().getIdentifier("text_" + id,"id", "com.jiwoolee.android_smartlectureroom");
        return resID;
    }
}