package com.jiwoolee.android_smartlectureroom.view.main;
import com.jiwoolee.android_smartlectureroom.base.BaseContract;

import androidx.recyclerview.widget.RecyclerView;

public interface MainContract {

    interface View extends BaseContract.View  {

    }

    interface Presenter extends BaseContract.Presenter<View> {

        void tokenUpdate();

        void setRecyclerview(RecyclerView recyclerview);
    }
}
