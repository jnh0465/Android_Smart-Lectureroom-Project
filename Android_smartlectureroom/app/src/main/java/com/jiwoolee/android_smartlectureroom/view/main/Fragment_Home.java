package com.jiwoolee.android_smartlectureroom.view.main;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.base.MyAdapter;
import com.jiwoolee.android_smartlectureroom.model.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class Fragment_Home extends Fragment {
    ViewPager viewPager;
    private MyAdapter adapter;
    private ArrayList<String> idList = new ArrayList<>();;
    private ArrayList<String> lectureList = new ArrayList<>();;
    private JSONArray jsonArray;

    public Fragment_Home() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton btn_logout = (ImageButton) view.findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        setRecyclerview(recyclerView);

        return view;
    }

    public void setRecyclerview(RecyclerView recyclerview) { /////////수정 및 분리 예정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FragmentActivity.mContext);
        recyclerview.setLayoutManager(linearLayoutManager);

        adapter = new MyAdapter();
        recyclerview.setAdapter(adapter);

        String str ="[{\"_id\":\"5d07b6b7f9a9919a2e20f664\",\"student_id\":\"2016100912\",\"student_password\":\"10\",\"student_name\":\"이지우\"," +
                "\"student_picture\":\"test_path...\",\"lecture_list\":[\"1\",\"2\",\"3\"],\"student_token\":\"" +
                "d6zd8syZ-0A:APA91bHnTLPvZqvO7CWKychzv0kuWf89IOBStWLBvMIpfijQ_8alJkY4lxfn7U8rUd-YDJAu0hYuzaWspPy0Hx6KACLUUnEg4Slg7AHzNeqNiqcscnli3uy_v7BGmdzndy2NPB_3WJPx\"}]";
        try {
            jsonArray = new JSONArray(str);
            for(int i = 0 ; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("student_id");
                String lecture_list = jsonObject.getString("lecture_list");
                idList.add(id);
                lectureList.add(lecture_list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        List<String> listTitle = idList;
//        List<String> listContent = lectureList;
        List<String> listTitle = Arrays.asList("알고리즘3", "알고리즘2", "알고리즘1", "자바스크립트2", "자바스크립트1");
        List<String> listContent = Arrays.asList("출석", "출석", "지각", "출석", "출석");

        for (int i = 0; i < listTitle.size(); i++) {
            Data data = new Data();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));
            adapter.addItem(data);
        }
        adapter.notifyDataSetChanged();
    }
}