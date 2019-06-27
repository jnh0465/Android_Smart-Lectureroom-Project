package com.jiwoolee.android_smartlectureroom.view.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.base.BaseActivity;
import com.jiwoolee.android_smartlectureroom.base.MyAdapter;
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.model.Data;
import com.jiwoolee.android_smartlectureroom.view.main.MainActivity;
import com.jiwoolee.android_smartlectureroom.view.main.MainContract;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduleActivity extends BaseActivity implements View.OnClickListener{
    private SchedulePresenter presenter = new SchedulePresenter();
    public static Context mContext;
    private MainContract.View view;
    private MyAdapter adapter;

    private ArrayList<String> dataList;
    private ArrayList<String> dayList;
    private ArrayList<String> timeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mContext = this;
        presenter.getSchedule();

        findViewById(R.id.button).setOnClickListener(this); //리스너 연결
        dataList = new ArrayList<>();
        dayList = new ArrayList<>();
        timeList = new ArrayList<>();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        RecyclerView recyclerView = findViewById(R.id.recyclerview2);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.mContext);
                        recyclerView.setLayoutManager(linearLayoutManager);

                        adapter = new MyAdapter();
                        recyclerView.setAdapter(adapter);

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

                        for (int i = 0; i < dataList.size(); i++) {
                            Data data = new Data();
                            data.setTitle(dayList.get(i)+timeList.get(i));
//                            data.setContent(timeList.get(i));
                            adapter.addItem(data);
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                3000);
    }

    @Override
    public void onClick(View v) { //버튼클릭시
        int i = v.getId();
        if (i == R.id.button) {
            Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}