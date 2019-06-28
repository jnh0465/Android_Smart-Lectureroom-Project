package com.jiwoolee.android_smartlectureroom.view.main;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jiwoolee.android_smartlectureroom.Fragment_Third;
import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.base.SectionPageAdapter;
import com.jiwoolee.android_smartlectureroom.base.BaseActivity;
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.model.IMyService;
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient;

import androidx.viewpager.widget.ViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class FragmentActivity extends BaseActivity {
    public static Context mContext;
    private final String TOPIC = "send"; //fcm firebase 토픽 선언

    private CompositeDisposable disposable;
    private IMyService iMyService;

    private ViewPager mViewPager;
    SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mContext = this;

        disposable = new CompositeDisposable();
        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = ((Retrofit) retrofitClient).create(IMyService.class);

        boolean isFirst = SharedPreferenceManager.getToken(mContext, "PREF_FIRST");
        if (!isFirst) {                                                      //최초 실행시
            getToken();
            tokenUpdate();
            SharedPreferenceManager.setToken(mContext, "PREF_FIRST", true);
        }
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(FragmentActivity.this, new OnSuccessListener<InstanceIdResult>() {
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

    public void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new Fragment_Home(), "홈");
        adapter.addFragment(new Fragment_Schedule(), "시간표");
        adapter.addFragment(new Fragment_Third(), "?");
        viewPager.setAdapter(adapter);
    }


    public void tokenUpdate() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        disposable.add(iMyService.sendToken(SharedPreferenceManager.getString(FragmentActivity.mContext, "PREF_ID"), SharedPreferenceManager.getString(FragmentActivity.mContext, "PREF_TOKEN"))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String response) throws Exception {
                                        //Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show(); //node 서버에서 response.json으로 보낸 응답 받아서 toast
                                        if (response.equals("1")) { //로그인 성공시
                                            Toast.makeText(FragmentActivity.mContext, "토큰이 등록되었습니다" + SharedPreferenceManager.getString(FragmentActivity.mContext, "PREF_TOKEN"), Toast.LENGTH_SHORT).show();
                                        } else if (response.equals("2")) {
                                            Toast.makeText(FragmentActivity.mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                        } else if (response.equals("0")) {
                                            Toast.makeText(FragmentActivity.mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        );
                    }
                },
                3000);
    }

    public void getSchedule() {
        disposable.add(iMyService.getSchedule(SharedPreferenceManager.getString(FragmentActivity.mContext, "PREF_ID"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String response) throws Exception {
//                        Toast.makeText(ScheduleActivity.mContext, response, Toast.LENGTH_SHORT).show();
                                SharedPreferenceManager.setString(FragmentActivity.mContext, "PREF_SC", response);
                            }
                        })
        );
    }
}
