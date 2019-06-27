package com.jiwoolee.android_smartlectureroom.view.schedule;
import com.jiwoolee.android_smartlectureroom.base.SharedPreferenceManager;
import com.jiwoolee.android_smartlectureroom.model.IMyService;
import com.jiwoolee.android_smartlectureroom.model.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SchedulePresenter implements ScheduleContract.Presenter {
    private ScheduleContract.View view;
    private CompositeDisposable disposable;
    private IMyService iMyService;

    SchedulePresenter() {
        this.disposable = new CompositeDisposable();

        Retrofit retrofitClient = RetrofitClient.getInstance();
        this.iMyService = ((Retrofit) retrofitClient).create(IMyService.class);
    }

    @Override
    public void setView(ScheduleContract.View view) {
        this.view = view;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void getSchedule() {
        disposable.add(iMyService.getSchedule(SharedPreferenceManager.getString(ScheduleActivity.mContext, "PREF_ID"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
//                        Toast.makeText(ScheduleActivity.mContext, response, Toast.LENGTH_SHORT).show();
                        SharedPreferenceManager.setString(ScheduleActivity.mContext, "PREF_SC", response);
                    }
                })
        );
    }
}
