package com.jiwoolee.android_smartlectureroom.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IMyService {
    @POST("student/process/register")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("student_id") String email, @Field("student_name") String name, @Field("student_password") String password);

    @POST("student/process/loginProcess")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("student_id") String email, @Field("student_password") String password);

    @POST("student/process/changePasswordProcess")
    @FormUrlEncoded
    Observable<String> changePasswordUser(@Field("student_id") String email, @Field("student_password") String password);
}
