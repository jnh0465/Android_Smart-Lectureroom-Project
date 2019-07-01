package com.jiwoolee.android_smartlectureroom.model

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IMyService {
    @POST("student/process/register")
    @FormUrlEncoded
    fun registerUser(@Field("student_id") email: String, @Field("student_name") name: String, @Field("student_password") password: String): Observable<String>

    @POST("student/process/loginProcess")
    @FormUrlEncoded
    fun loginUser(@Field("student_id") email: String, @Field("student_password") password: String): Observable<String>

    @POST("student/process/changePasswordProcess")
    @FormUrlEncoded
    fun changePasswordUser(@Field("student_id") email: String, @Field("student_password") password: String): Observable<String>

    @POST("student/process/getTokenProcess")
    @FormUrlEncoded
    fun sendToken(@Field("student_id") email: String, @Field("student_token") token: String): Observable<String>

    @POST("student/process/getScheduleProcess")
    @FormUrlEncoded
    fun getSchedule(@Field("student_id") email: String): Observable<String>
}
