package com.trycath.myupdateapklibrary.listener;

import com.trycath.myupdateapklibrary.model.AppInfoModel;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-18 11:15
 * @version: V1.0 <描述当前版本功能>
 */

public interface AppUpdateListener {
    static final String TAG = "AppUpdateListener";
    void onStart();
    void onCompleted();
    void onError(Throwable e);
    void onNext(AppInfoModel appInfoModel);
    void onNext(AppInfoModel appInfoModel,int state);
}