package com.jiwoolee.android_smartlectureroom.view.main;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jiwoolee.android_smartlectureroom.model.Data;
import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.base.BaseActivity;
import com.jiwoolee.android_smartlectureroom.view.schedule.ScheduleActivity;
import com.jiwoolee.android_smartlectureroom.view.login.LoginActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private MainPresenter presenter = new MainPresenter();
    public static Context mContext;
    private MainContract.View view;

    private final String TOPIC = "send"; //fcm firebase 토픽 선언

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        findViewById(R.id.btn_logout).setOnClickListener(this); //리스너 연결
        findViewById(R.id.imageButton3).setOnClickListener(this);

        boolean isFirst = SharedPreferenceManager.getToken(mContext, "PREF_FIRST");
        if (!isFirst) {                                                      //최초 실행시
            getToken();
            presenter.tokenUpdate();
            SharedPreferenceManager.setToken(mContext, "PREF_FIRST", true);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        presenter.setRecyclerview(recyclerView);
    }

    //listener//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) { //버튼클릭시
        int i = v.getId();
        if (i == R.id.btn_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            SharedPreferenceManager.clear(mContext);
        }else if(i==R.id.imageButton3){
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        }
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();  //토큰 생성
                SharedPreferenceManager.setString(mContext, "PREF_TOKEN", token);
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);  //알림허용
    }

    @Override
    public void onBackPressed() { //뒤로가기 막기
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.releaseView(); // presenter 연결 해제
    }

}