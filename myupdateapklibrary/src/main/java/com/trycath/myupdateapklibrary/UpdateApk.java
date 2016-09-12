package com.trycath.myupdateapklibrary;

import android.content.Context;
import android.util.Log;

import com.trycath.myupdateapklibrary.dialogactivity.PromptDialogActivity;
import com.trycath.myupdateapklibrary.httprequest.DownloadServiceApi;
import com.trycath.myupdateapklibrary.listener.ServiceGenerator;
import com.trycath.myupdateapklibrary.model.AppInfoModel;

import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-12 14:02
 * @version: V1.0 <描述当前版本功能>
 */

public class UpdateApk {
    private static final String TAG = "UpdateApk";
    private static volatile UpdateApk sInst = null;
    Subscription subscription;
    //getInstance()
    public static UpdateApk init(Context context) {
        UpdateApk inst = sInst;
        if (inst == null) {
            synchronized (UpdateApk.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new UpdateApk(context);
                    sInst = inst;
                }
            }
        }
        return inst;
    }
    
    private UpdateApk(final Context context) {
        Log.d(TAG, "GETAPPINFO");
        DownloadServiceApi downloadService = ServiceGenerator.createService(DownloadServiceApi.class);
        subscription = downloadService.getUpdateApkInfo(UpdateKey.APP_ID,UpdateKey.API_TOKEN)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .subscribe(new Subscriber<AppInfoModel>() {
            @Override
            public void onCompleted() {
                
            }
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, e.toString());
            }
            @Override
            public void onNext(AppInfoModel appInfoModel) {
                Log.d(TAG, appInfoModel.toString());
                PromptDialogActivity.startActivity(context,appInfoModel.getInstallUrl());
            }
        });
    }
}