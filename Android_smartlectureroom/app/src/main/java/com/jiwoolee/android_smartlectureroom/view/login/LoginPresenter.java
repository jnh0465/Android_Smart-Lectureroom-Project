package com.jiwoolee.android_smartlectureroom.view.login;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.model.IMyService;
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private CompositeDisposable disposable;
    private IMyService iMyService;

    LoginPresenter() {
        this.disposable = new CompositeDisposable();

        Retrofit retrofitClient = RetrofitClient.getInstance();
        this.iMyService = ((Retrofit) retrofitClient).create(IMyService.class);
    }

    @Override
    public void setView(LoginContract.View view) {
        this.view = view;
        view.autoLogin();
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void loginUser(final String student_id, final String student_password) {
        disposable.add(iMyService.loginUser(student_id, student_password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        if(response.equals("1")){ //로그인 성공시
                            view.startActivity();
                            SharedPreferenceManager.setString(LoginActivity.mContext, "PREF_ID", student_id);
                            SharedPreferenceManager.setString(LoginActivity.mContext, "PREF_PW", student_password);
//                            Toast.makeText(LoginActivity.mContext, SharedPreferenceManager.getString(LoginActivity.mContext,"PREF_ID")+
//                                    SharedPreferenceManager.getString(LoginActivity.mContext,"PREF_PW"), Toast.LENGTH_SHORT).show();

                        }else if(response.equals("2")){
                            Toast.makeText(LoginActivity.mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }else if(response.equals("0")){
                            Toast.makeText(LoginActivity.mContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );
    }

    @Override
    public void findPassword(String student_id, String student_password) {
        disposable.add(iMyService.changePasswordUser(student_id, student_password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        if(response.equals("1")){ //로그인 성공시
                            Toast.makeText(LoginActivity.mContext, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        }else if(response.equals("2")){
                            Toast.makeText(LoginActivity.mContext, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );
    }

    @Override
    public boolean validateForm(TextView textview_id, TextView textview_pw) {
        boolean valid = true;
        String id = textview_id.getText().toString();
        String pw = textview_pw.getText().toString();

        if (TextUtils.isEmpty(id)) {
            textview_id.setError("학번을 입력해주세요");
            valid = false;
        } else if (TextUtils.isEmpty(pw)) {
            textview_id.setError(null);
            textview_pw.setError("비밀번호를 입력해주세요");
            valid = false;
        } else {
            textview_pw.setError(null);
        }
        return valid;
    }
}
