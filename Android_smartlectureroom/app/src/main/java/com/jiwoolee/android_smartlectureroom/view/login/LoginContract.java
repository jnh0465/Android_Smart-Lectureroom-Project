package com.jiwoolee.android_smartlectureroom.view.login;

import android.widget.TextView;

import com.jiwoolee.android_smartlectureroom.base.BaseContract;

public interface LoginContract {

    interface View extends BaseContract.View  {
        void autoLogin();

        void startActivity();
    }

    interface Presenter extends BaseContract.Presenter<View> {
        @Override
        void setView(View view);

        @Override
        void releaseView();

        void loginUser(final String student_id, final String student_password);

        void findPassword(String student_id, String student_password);

        boolean validateForm(TextView textview_id, TextView textview_pw);
    }
}
