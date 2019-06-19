package com.jiwoolee.android_smartlectureroom;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
import com.jiwoolee.android_smartlectureroom.Retrofit.IMyService;
import com.jiwoolee.android_smartlectureroom.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static Context mContext;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IMyService iMyService;

    private TextView edit_login_id, edit_login_password, edit_register_id, edit_register_password2, edit_register_password, text_create;
    private Button btn_login;
    private CheckBox checkBox;

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = ((Retrofit) retrofitClient).create(IMyService.class);

        edit_login_id = findViewById(R.id.edit_id);
        edit_login_password = findViewById(R.id.edit_password);
        btn_login = findViewById(R.id.btn_login);
        text_create = findViewById(R.id.text_findpw);

        btn_login.setOnClickListener(this); //리스너 연결
        text_create.setOnClickListener(this);

        checkBox = findViewById(R.id.autologin_checkBox); //체크박스 리스너 연결
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);

        String pref_student_id = SharedPreferenceManager.getString(mContext, "PREF_ID");
        String pref_student_pw = SharedPreferenceManager.getString(mContext, "PREF_PW");
        boolean pref_checkbox_state = SharedPreferenceManager.getBoolean(mContext, "PREF_CB");

        if(pref_checkbox_state) {   //어플을 껐다 켰을 때 스위치 상태를 적용하기 위해  내용확인,
            checkBox.setChecked(true);                                       //true면 체크
        } else {
            checkBox.setChecked(false);                                      //false면 체크x
        }

        if(pref_student_id.length() != 0 && pref_checkbox_state) {
            showProgressDialog(); //프로그래스바 보이기
            loginUser(pref_student_id, pref_student_pw);
        }
    }


    //listener//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) { //버튼클릭시
        int i = v.getId();
        if (i == R.id.btn_login) {
            boolean test = validateForm(); //폼 채움 여부 확인
            if(test){
                loginUser(edit_login_id.getText().toString(), edit_login_password.getText().toString());
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

    /* 폼이채워지지않아도(validateForm_find() false) dialog가 꺼지는 현상발생
       alertdialog로 바꿔 onPositiveButton을 중첩시켜서 처리하던가 dialog가 아닌 activity로(추후 문자인증까지 욕심내면) 처리해야할듯
       일단 나중에 할래... 19/06/18 1702
    */
    private void MaterialDialog(){
        final View register_layout = LayoutInflater.from(LoginActivity.this).inflate(R.layout.findpw_layout, null);
        new MaterialStyledDialog.Builder(LoginActivity.this)
                .setTitle("FIND PASSWORD")
                .setCustomView(register_layout)
                .setNegativeText("CANSEL")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("SEND")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        edit_register_id = register_layout.findViewById(R.id.edit_id);
                        edit_register_password = register_layout.findViewById(R.id.edit_password);

                        boolean test = validateForm_find(); //폼 채움 여부 확인
                        if(test){
                            findPassword(edit_register_id.getText().toString(), edit_register_password.getText().toString());
                        }
                    }
                }).show();
    }

    public void findPassword(String student_id, String student_password) {
        compositeDisposable.add(iMyService.changePasswordUser(student_id, student_password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        if(response.equals("1")){ //로그인 성공시
                            showProgressDialog(); //프로그래스바 보이기
                            Toast.makeText(mContext, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        }else if(response.equals("2")){
                            Toast.makeText(mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );
    }

    private boolean validateForm_find() { //로그인 폼 채움 여부
        boolean valid = true;

        String id = edit_register_id.getText().toString();
        String pw = edit_register_password.getText().toString();

        if (TextUtils.isEmpty(id)) {
            edit_register_id.setError("학번을 입력해주세요");
            valid = false;
        } else {
            edit_register_id.setError(null);
        }

        if (TextUtils.isEmpty(pw)) {
            edit_register_password.setError("비밀번호를 입력해주세요");
            valid = false;
        } else {
            edit_register_password.setError(null);
        }
        return valid;
    }

    private boolean validateForm() { //로그인 폼 채움 여부
        boolean valid = true;

        String id = edit_login_id.getText().toString();
        String pw = edit_login_password.getText().toString();

        if (TextUtils.isEmpty(id)) {
            edit_login_id.setError("학번을 입력해주세요");
            valid = false;
        } else {
            edit_login_id.setError(null);
        }

        if (TextUtils.isEmpty(pw)) {
            edit_login_password.setError("비밀번호를 입력해주세요");
            valid = false;
        } else {
            edit_login_password.setError(null);
        }
        return valid;
    }

    public void loginUser(final String student_id, final String student_password) {
        compositeDisposable.add(iMyService.loginUser(student_id, student_password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        //Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show(); //node 서버에서 response.json으로 보낸 응답 받아서 toast
                        if(response.equals("1")){ //로그인 성공시
                            Intent intent = new Intent(mContext, MainActivity.class);
                            startActivity(intent);
                            SharedPreferenceManager.setString(mContext, "PREF_ID", student_id);
                            SharedPreferenceManager.setString(mContext, "PREF_PW", student_password);
                            Toast.makeText(mContext, SharedPreferenceManager.getString(mContext,"PREF_ID")+
                                    SharedPreferenceManager.getString(mContext,"PREF_PW"), Toast.LENGTH_SHORT).show();

                        }else if(response.equals("2")){
                            Toast.makeText(mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }else if(response.equals("0")){
                            Toast.makeText(mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );
    }
}
