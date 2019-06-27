package com.jiwoolee.android_smartlectureroom.base;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.jiwoolee.android_smartlectureroom.R;
import com.jiwoolee.android_smartlectureroom.view.login.LoginActivity;
import com.jiwoolee.android_smartlectureroom.view.login.LoginPresenter;

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.myDialog);
            mProgressDialog.addContentView(new ProgressBar(this),
                    new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void MaterialDialog(final LoginPresenter presenter){
        final View register_layout = LayoutInflater.from(LoginActivity.mContext).inflate(R.layout.findpw_layout, null);
        new MaterialStyledDialog.Builder(LoginActivity.mContext)
                .setTitle("FIND PASSWORD")
                .setCustomView(register_layout)
                .setNegativeText("CANSEL")
                .onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("SEND")
                .onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        TextView edit_register_id = register_layout.findViewById(R.id.edit_id);
                        TextView edit_register_password = register_layout.findViewById(R.id.edit_password);
                        TextView edit_register_password2 = register_layout.findViewById(R.id.edit_password2);

                        boolean test = presenter.validateForm(edit_register_id,edit_register_password); //폼 채움 여부 확인
                        if(test&&(edit_register_password==edit_register_password2)){
                            presenter.findPassword(edit_register_id.getText().toString(), edit_register_password.getText().toString());
                        }
                    }
                }).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}