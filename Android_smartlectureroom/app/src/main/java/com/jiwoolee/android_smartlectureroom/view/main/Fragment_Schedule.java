package com.jiwoolee.android_smartlectureroom.view.main;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class Fragment_Schedule extends Fragment {
    ViewPager viewPager;

    private ArrayList<String> dataList;
    private ArrayList<String> dayList;
    private ArrayList<String> timeList;
    private ArrayList<String> timeList22222;

    private  TextView textView;

    public Fragment_Schedule() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        dataList = new ArrayList<>();
        dayList = new ArrayList<>();
        timeList = new ArrayList<>();
        timeList22222 = new ArrayList<>();

        ((FragmentActivity)getActivity()).getSchedule();

        int b = getId(12);
        textView = (TextView) view.findViewById(b);

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

                        String str = SharedPreferenceManager.getString(FragmentActivity.mContext,"PREF_SC");
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        textView.setText("안녀엉");
        textView.setLayoutParams(new TableRow.LayoutParams(3)); //layout_column
    }

    private int getId(int id){
        int resID = getResources().getIdentifier("text_" + id,"id", "com.jiwoolee.android_smartlectureroom");
        return resID;
    }
}