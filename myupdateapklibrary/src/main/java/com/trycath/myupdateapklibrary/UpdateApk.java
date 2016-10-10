package com.trycath.myupdateapklibrary;

import android.content.Context;
import android.util.Log;

import com.trycath.myupdateapklibrary.dialogactivity.PromptDialogActivity;
import com.trycath.myupdateapklibrary.httprequest.DownloadServiceApi;
import com.trycath.myupdateapklibrary.listener.AppUpdateListener;
import com.trycath.myupdateapklibrary.listener.ServiceGenerator;
import com.trycath.myupdateapklibrary.model.AppInfoModel;
import com.trycath.myupdateapklibrary.util.GetAppInfo;
import com.trycath.myupdateapklibrary.util.IntenetUtil;
import com.trycath.myupdateapklibrary.util.PreferenceUtils;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
    private static volatile Subscription subscription;
    private static Context mContext;
    private static AppUpdateListener appUpdateListener = null;
    private static boolean mManualUpdateFlg = false;
    
    public static UpdateApk init(Context context) {
        UpdateApk inst = sInst;
        if (inst == null) {
            synchronized (UpdateApk.class) {
                inst = sInst;
                if (inst == null) {
                    mContext = context;
                    inst = new UpdateApk(context);
                    sInst = inst;
                }else{
                    sInst.update();
                }
            }
        }else{
            sInst.update();
        }
        return inst;
    }

    public static void setAppUpdateListener(AppUpdateListener listener) {
        appUpdateListener = listener;
    }

    public static void setmManualUpdateFlg(boolean mManualUpdateFlg) {
        UpdateApk.mManualUpdateFlg = mManualUpdateFlg;
    }
    public static void setmManualUpdate(boolean mManualUpdateFlg,AppUpdateListener listener) {
        UpdateApk.mManualUpdateFlg = mManualUpdateFlg;
        appUpdateListener = listener;
    }

    private UpdateApk(final Context context) {
        Log.d(TAG, "UpdateApk");
        update();
    }
    private void update(){
        Log.d(TAG, "GETAPPINFO");
        if(UpdateKey.DOWNLOAD_WIFI){
            switch (IntenetUtil.getNetworkState(mContext)){
                case IntenetUtil.NETWORN_NONE:
                    Log.d(TAG,"IntenetUtil.NETWORN_NONE");
                    break;
                case IntenetUtil.NETWORN_2G:
                    Log.d(TAG,"IntenetUtil.NETWORN_2G");
                    break;
                case IntenetUtil.NETWORN_3G:
                    Log.d(TAG,"IntenetUtil.NETWORN_3G");
                    break;
                case IntenetUtil.NETWORN_4G:
                    Log.d(TAG,"IntenetUtil.NETWORN_4G");
                    break;
                case IntenetUtil.NETWORN_MOBILE:
                    Log.d(TAG,"IntenetUtil.NETWORN_MOBILE");
                    break;
                case IntenetUtil.NETWORN_WIFI:
                    Log.d(TAG,"IntenetUtil.NETWORN_WIFI");
                    getAppinfo();
                    break;
                default:
            }
        }else{
            getAppinfo();
        }
    }
    
    public void getAppinfo(){
        DownloadServiceApi downloadService = ServiceGenerator.createService(DownloadServiceApi.class);
        subscription = downloadService.getUpdateApkInfo(UpdateKey.APP_ID,UpdateKey.API_TOKEN)
                .delaySubscription(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AppInfoModel>() {
                @Override
                public void onStart() {
                    super.onStart();
                    if(appUpdateListener!=null){
                        appUpdateListener.onStart();
                    }
                }

                @Override
                public void onCompleted() {
                    if(appUpdateListener!=null){
                        appUpdateListener.onCompleted();
                    }
                    mManualUpdateFlg = false;

                }
                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, e.toString());
                    if(appUpdateListener!=null){
                        appUpdateListener.onError(e);
                    }
                    mManualUpdateFlg = false;
                }
                @Override
                public void onNext(AppInfoModel appInfoModel) {
                    Log.d(TAG, appInfoModel.toString());
                    if(appUpdateListener!=null){
                        appUpdateListener.onNext(appInfoModel);
                    }
                    valAppInfo(appInfoModel);
                    mManualUpdateFlg = false;
                    
                }
            });
    }
    
    public void valAppInfo(AppInfoModel appInfoModel){
        if(appInfoModel.getVersion()!=null){
            if(!PreferenceUtils.getPrefBoolean(mContext,appInfoModel.getVersion(),false) || mManualUpdateFlg){
                switch (GetAppInfo.compareVersionCode(GetAppInfo.getVersionCode(mContext),Integer.parseInt(appInfoModel.getVersion()))){
                    case UpdateState.BEST_NEW_VSERSION:
                        Log.d(TAG,"this is best new version");
                        if(appUpdateListener!=null){
                            appUpdateListener.onNext(appInfoModel,0);
                        }
                        break;
                    case UpdateState.BEST_HEIGHT_VERSION:
                        Log.d(TAG,"this is highest version");
                        if(appUpdateListener!=null){
                            appUpdateListener.onNext(appInfoModel,1);
                        }
                        break;
                    case UpdateState.NEED_UPDATE_VERSION:
                        Log.d(TAG,"need update new version");
                        PromptDialogActivity.startActivity(mContext,appInfoModel);
                        if(appUpdateListener!=null){
                            appUpdateListener.onNext(appInfoModel,-1);
                        }
                        break;
                    default:

                }
            }
        }
    }

    public static void destory() {
        if (sInst!=null) {
           sInst = null;
        }
        if(mContext!=null){
            mContext=null;
        }
        if (subscription!=null && !subscription.isUnsubscribed()){
           subscription.unsubscribe();
           subscription = null;
        }
        if(appUpdateListener!=null){
            appUpdateListener=null;
        }
    }
}