package com.trycath.myupdateapklibrary.httprequest;

import com.trycath.myupdateapklibrary.model.AppInfoModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-6 12:10
 * @version: V1.0 <描述当前版本功能>
 */
public interface DownloadServiceApi {
    @GET
    Observable<ResponseBody> download(@Url String url);

    @GET("/apps/latest/{app_id}")
    Observable<AppInfoModel> getUpdateApkInfo(@Path("app_id") String app_id , @Query("api_token") String api_token);
    
    @GET
    Call<List<ResponseBody>> downloadSync(@Url String url);
}
