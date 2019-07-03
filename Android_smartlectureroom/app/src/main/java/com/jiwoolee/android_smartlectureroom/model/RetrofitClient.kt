package com.jiwoolee.android_smartlectureroom.model

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private var instance: Retrofit? = null
    fun getInstance(): Retrofit? {
        if (instance == null) {
            instance = Retrofit.Builder().baseUrl("http://192.168.195.108:3000/")
                    //http://localhost:3000/
                    //http://10.0.2.2:3000/ 에뮬레이터
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
        return instance
    }
}
