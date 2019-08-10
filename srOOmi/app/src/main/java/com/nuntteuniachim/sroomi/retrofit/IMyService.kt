package com.nuntteuniachim.sroomi.retrofit

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IMyService {
    @POST("student/process/register")
    @FormUrlEncoded
    fun registerUser(@Field("student_id") email: String, @Field("student_name") name: String, @Field("student_password") password: String): Observable<String>

    @POST("student/process/loginProcess") //로그인
    @FormUrlEncoded
    fun loginUser(@Field("student_id") email: String, @Field("student_password") password: String): Observable<String>

    @POST("student/process/changePasswordProcess") //비밀번호찾기
    @FormUrlEncoded
    fun changePasswordUser(@Field("student_id") email: String, @Field("student_password") password: String): Observable<String>

    @POST("student/process/getTokenProcess") //토큰전송
    @FormUrlEncoded
    fun sendToken(@Field("student_id") email: String, @Field("student_token") token: String): Observable<String>

    @POST("student/process/getScheduleProcess") //시간표
    @FormUrlEncoded
    fun getSchedule(@Field("student_id") email: String): Observable<String>

    @POST("student/process/getAttendStateProcess") //출석로그
    @FormUrlEncoded
    fun getAttendStateProcess(@Field("student_id") email: String): Observable<String>

    @POST("student/process/pushResponseProcess") //푸시알림
    @FormUrlEncoded
    fun pushResponse(@Field("student_attendstate") attend: String): Observable<String>

    @POST("student/process/upload")
    @FormUrlEncoded
    fun postId(@Field("student_id") id: String): Observable<String>

    @Multipart
    @POST("student/process/upload") //사진업로드
    fun postImage(@Part image: MultipartBody.Part, @Part("upload") name: RequestBody): Call<ResponseBody>
}
