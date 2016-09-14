package com.trycath.myupdateapklibrary.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trycath.myupdateapklibrary.UpdateKey;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-12 14:52
 * @version: V1.0 <描述当前版本功能>
 */

public class ServiceGenerator {
    private static final String HOST = UpdateKey.BASE_URL;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").serializeNulls().create();
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(HOST)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    public static <T> T createService(Class<T> tClass){
        return builder
                .client(HttpClientHelper.getOkhttp())
                .build()
                .create(tClass);
    }

    /**
     * 创建带响应进度(下载进度)回调的service
     */
    public static <T> T createResponseService(Class<T> tClass, ProgressResponseListener listener){
        return builder.client(HttpClientHelper.addProgressResponseListener(listener)).build().create(tClass);
    }
}