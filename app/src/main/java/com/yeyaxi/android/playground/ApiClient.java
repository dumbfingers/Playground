package com.yeyaxi.android.playground;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private EndPoint endPoint;

    public ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EndPoint.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.endPoint = retrofit.create(EndPoint.class);
    }

    public EndPoint getEndPoint() {
        return this.endPoint;
    }
}
