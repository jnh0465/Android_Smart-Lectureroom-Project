package com.jiwoolee.android_smartlectureroom;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.jiwoolee.android_smartlectureroom.base.BaseActivity;

import androidx.annotation.Nullable;

public class LoginActivity extends BaseActivity implements MainContract.View, View.OnClickListener{

    private MainPresenter presenter = new MainPresenter();

    public static Context mContext;
    private TextView edit_login_id, edit_login_password, edit_register_id, edit_register_password2, edit_register_password, text_create;
    private Button btn_login;
    private CheckBox checkBox;
    private MainContract.View view;

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

        String pref_student_id = SharedPreferenceManager.getString(mContext, "PREF_ID"); //SharedPreference
        String pref_student_pw = SharedPreferenceManager.getString(mContext, "PREF_PW");
        boolean pref_checkbox_state = SharedPreferenceManager.getBoolean(mContext, "PREF_CB");

        if(pref_checkbox_state) {   //어플을 껐다 켰을 때 스위치 상태를 적용하기 위해  내용확인,
            checkBox.setChecked(true);                                       //true면 체크
        } else {
            checkBox.setChecked(false);                                      //false면 체크x
        }

        if(pref_student_id.length() != 0 && pref_checkbox_state) { //자동로그인시
            showProgressDialog(); //프로그래스바 보이기
            presenter.loginUser(pref_student_id, pref_student_pw);
        }
    }

    @Override
    public void showToast() { //
        Toast.makeText(LoginActivity.mContext, "토스트 안녕", Toast.LENGTH_SHORT).show();
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
            MaterialDialog();
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

    private void MaterialDialog(){
        final View register_layout = LayoutInflater.from(LoginActivity.this).inflate(R.layout.findpw_layout, null);
        new MaterialStyledDialog.Builder(LoginActivity.this)
                .setTitle("FIND PASSWORD")
                .setCustomView(register_layout)
                .setNegativeText("CANSEL")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("SEND")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        edit_register_id = register_layout.findViewById(R.id.edit_id);
                        edit_register_password = register_layout.findViewById(R.id.edit_password);

                        boolean test = presenter.validateForm(edit_register_id,edit_register_password); //폼 채움 여부 확인
                        if(test){
                            presenter.findPassword(edit_register_id.getText().toString(), edit_register_password.getText().toString());
                        }
                    }
                }).show();
    }

    @Override
    public void onStop() {
        super.onStop();

        // presenter 와의 연결을 해제합니다.
        presenter.releaseView();
    }

}
