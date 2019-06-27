package com.jiwoolee.android_smartlectureroom.view.schedule;
import com.jiwoolee.android_smartlectureroom.base.BaseContract;

public interface ScheduleContract {

    interface View extends BaseContract.View  {

    }

    interface Presenter extends BaseContract.Presenter<View> {

        void getSchedule();
    }
}
