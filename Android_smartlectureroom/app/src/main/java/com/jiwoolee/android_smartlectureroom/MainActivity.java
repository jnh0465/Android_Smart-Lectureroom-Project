package com.jiwoolee.android_smartlectureroom;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        findViewById(R.id.btn_logout).setOnClickListener(this); //리스너 연결
        Toast.makeText(mContext, SharedPreferenceManager.getString(mContext,"PREF_ID")+
                SharedPreferenceManager.getString(mContext,"PREF_PW"), Toast.LENGTH_SHORT).show();
    }

    //listener//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) { //버튼클릭시
        int i = v.getId();
        if (i == R.id.btn_logout) {
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            SharedPreferenceManager.clear(mContext);
        }
    }
}

