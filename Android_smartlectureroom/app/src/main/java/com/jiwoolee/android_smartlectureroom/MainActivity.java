package com.jiwoolee.android_smartlectureroom;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jiwoolee.android_smartlectureroom.Retrofit.IMyService;
import com.jiwoolee.android_smartlectureroom.Retrofit.RetrofitClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IMyService iMyService;

    private final String TOPIC = "send";                //fcm firebase 토픽 선언

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = ((Retrofit) retrofitClient).create(IMyService.class);

        findViewById(R.id.btn_logout).setOnClickListener(this); //리스너 연결
        Toast.makeText(mContext, SharedPreferenceManager.getString(mContext, "PREF_ID") +
                SharedPreferenceManager.getString(mContext, "PREF_PW"), Toast.LENGTH_SHORT).show();

        boolean isFirst = SharedPreferenceManager.getToken(mContext, "PREF_FIRST");
        if (!isFirst) {                                                      //최초 실행시
            getToken(); //토큰 얻기
            SharedPreferenceManager.setToken(mContext, "PREF_FIRST", true);
        }
    }

    //listener//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) { //버튼클릭시
        int i = v.getId();
        if (i == R.id.btn_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            SharedPreferenceManager.clear(mContext);
        }
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();  //토큰 생성
                SharedPreferenceManager.setString(mContext, "PREF_TOKEN", token);
                Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                Log.d("aaaaaaaa", token);
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);  //알림허용(디폴트)
        tokenUpdate();
    }

    private void tokenUpdate() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        compositeDisposable.add(iMyService.sendToken(SharedPreferenceManager.getString(mContext, "PREF_ID"), SharedPreferenceManager.getString(mContext, "PREF_TOKEN"))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String response) throws Exception {
                                        //Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show(); //node 서버에서 response.json으로 보낸 응답 받아서 toast
                                        if (response.equals("1")) { //로그인 성공시
                                            Toast.makeText(mContext, "토큰이 등록되었습니다" + SharedPreferenceManager.getString(mContext, "PREF_TOKEN"), Toast.LENGTH_SHORT).show();
                                        } else if (response.equals("2")) {
                                            Toast.makeText(mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                        } else if (response.equals("0")) {
                                            Toast.makeText(mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        );
                    }
                },
                3000);
    }

    @Override
    public void onBackPressed() { //뒤로가기 막기
        //super.onBackPressed();
    }
}