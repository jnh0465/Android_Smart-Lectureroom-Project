package com.jiwoolee.android_smartlectureroom.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity;
import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.base.BaseActivity;

import androidx.annotation.Nullable;

public class LoginActivity extends BaseActivity implements LoginContract.View, View.OnClickListener{
    private LoginPresenter presenter = new LoginPresenter();
    public static Context mContext;

    private TextView edit_login_id, edit_login_password, text_create;
    private Button btn_login;
    private CheckBox checkBox;
    private LoginContract.View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;

        presenter.setView(this); // presenter 연결

        edit_login_id = findViewById(R.id.edit_id); //
        edit_login_password = findViewById(R.id.edit_password);
        btn_login = findViewById(R.id.btn_login);
        text_create = findViewById(R.id.text_findpw);

        btn_login.setOnClickListener(this); //리스너 연결
        text_create.setOnClickListener(this);
        checkBox = findViewById(R.id.autologin_checkBox);
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);

        boolean pref_checkbox_state = SharedPreferenceManager.getBoolean(mContext, "PREF_CB"); //SharedPreference
        if(pref_checkbox_state) {   //어플을 껐다 켰을 때 스위치 상태를 적용하기 위해  내용확인,
            checkBox.setChecked(true);                                       //true면 체크
        } else {
            checkBox.setChecked(false);                                      //false면 체크x
        }
    }

    //listener//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void onClick(View v) { //버튼클릭시
        int i = v.getId();
        if (i == R.id.btn_login) {
            boolean test = presenter.validateForm(edit_login_id, edit_login_password); //폼 채움 여부 확인
            if(test){
                presenter.loginUser(edit_login_id.getText().toString(), edit_login_password.getText().toString());
            }
        }else if(i == R.id.text_findpw){
            MaterialDialog(presenter);
        }
    }

    public CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() { //체크박스 체크 클릭시
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){ //체크시
                SharedPreferenceManager.setBoolean(mContext, "PREF_CB", true);
            } else{
                SharedPreferenceManager.setBoolean(mContext, "PREF_CB", false);
            }
        }
    };

    @Override
    public void autoLogin() {
        String pref_student_id = SharedPreferenceManager.getString(mContext, "PREF_ID"); //SharedPreference
        String pref_student_pw = SharedPreferenceManager.getString(mContext, "PREF_PW");
        boolean pref_checkbox_state = SharedPreferenceManager.getBoolean(mContext, "PREF_CB");
        if(pref_student_id.length() != 0 && pref_checkbox_state) { //자동로그인시
            showProgressDialog(); //프로그래스바 보이기
            presenter.loginUser(pref_student_id, pref_student_pw);
        }
    }

    @Override
    public void startActivity() {
        Intent intent = new Intent(mContext, FragmentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.releaseView(); // presenter 연결 해제
    }

}
