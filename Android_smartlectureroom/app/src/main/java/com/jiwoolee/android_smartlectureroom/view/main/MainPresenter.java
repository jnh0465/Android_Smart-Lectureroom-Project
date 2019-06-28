package com.jiwoolee.android_smartlectureroom.view.main;
import android.widget.Toast;

import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.base.MyAdapter;
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.model.Data;
import com.jiwoolee.android_smartlectureroom.model.IMyService;
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;
    private CompositeDisposable disposable;
    private IMyService iMyService;

    private MyAdapter adapter;
    private ArrayList<String> idList = new ArrayList<>();;
    private ArrayList<String> lectureList = new ArrayList<>();;
    private JSONArray jsonArray;

    MainPresenter() {
        this.disposable = new CompositeDisposable();

        Retrofit retrofitClient = RetrofitClient.getInstance();
        this.iMyService = ((Retrofit) retrofitClient).create(IMyService.class);
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void tokenUpdate() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        disposable.add(iMyService.sendToken(SharedPreferenceManager.getString(MainActivity.mContext, "PREF_ID"), SharedPreferenceManager.getString(MainActivity.mContext, "PREF_TOKEN"))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String response) throws Exception {
                                        //Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show(); //node 서버에서 response.json으로 보낸 응답 받아서 toast
                                        if (response.equals("1")) { //로그인 성공시
                                            Toast.makeText(MainActivity.mContext, "토큰이 등록되었습니다" + SharedPreferenceManager.getString(MainActivity.mContext, "PREF_TOKEN"), Toast.LENGTH_SHORT).show();
                                        } else if (response.equals("2")) {
                                            Toast.makeText(MainActivity.mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                        } else if (response.equals("0")) {
                                            Toast.makeText(MainActivity.mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        );
                    }
                },
                3000);
    }


    @Override
    public void setRecyclerview(RecyclerView recyclerview) { /////////수정 및 분리 예정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.mContext);
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
