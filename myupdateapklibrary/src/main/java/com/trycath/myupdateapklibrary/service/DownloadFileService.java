package com.trycath.myupdateapklibrary.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.trycath.myupdateapklibrary.R;
import com.trycath.myupdateapklibrary.UpdateKey;
import com.trycath.myupdateapklibrary.dialogactivity.ProgressBarActivity;
import com.trycath.myupdateapklibrary.dialogactivity.PromptDialogActivity;
import com.trycath.myupdateapklibrary.exception.CustomizeException;
import com.trycath.myupdateapklibrary.httprequest.DownloadServiceApi;
import com.trycath.myupdateapklibrary.listener.ProgressResponseListener;
import com.trycath.myupdateapklibrary.listener.ServiceGenerator;
import com.trycath.myupdateapklibrary.model.AppInfoModel;
import com.trycath.myupdateapklibrary.model.DownloadModel;
import com.trycath.myupdateapklibrary.rxbus.RxBus;
import com.trycath.myupdateapklibrary.rxbus.RxBusResult;
import com.trycath.myupdateapklibrary.util.FileUtils;
import com.trycath.myupdateapklibrary.util.GetAppInfo;
import com.trycath.myupdateapklibrary.util.InstallApk;
import com.trycath.myupdateapklibrary.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-14 14:39
 * @version: V1.0 <描述当前版本功能>
 */

public class DownloadFileService extends Service implements ProgressResponseListener {
    private static final String TAG = "DownloadFileService";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private Subscription subscription;
    private RxBus rxBus = RxBus.getInstance();
    private AppInfoModel appInfoModel;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        if(intent!=null && intent.getExtras()!=null && intent.getExtras().getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL)!=null){
            appInfoModel = (AppInfoModel) intent.getExtras().getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL);
            if(UpdateKey.WITH_NOTIFITION){
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(getResources().getString(R.string.file_downloading))
                        .setSmallIcon(GetAppInfo.getAppIconId(this))
                        .setContentText(getResources().getString(R.string.file_downloading))
                        .setTicker(getResources().getString(R.string.start_downloaded))
                        .setAutoCancel(true);
                notificationManager.notify(0, notificationBuilder.build());
            }
            coloseDownload();
            download();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG,"onStart()");
    }

    private void download() {
        Log.d(TAG,"download");
        if(UpdateKey.WITH_DIALOG){
            ProgressBarActivity.startActivity(this,appInfoModel);
        }
        DownloadServiceApi downloadService = ServiceGenerator.createResponseService(DownloadServiceApi.class, this);
        subscription = downloadService.download(appInfoModel.getInstallUrl())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, InputStream>() {
                    @Override
                    public InputStream call(ResponseBody responseBody) {
                        return responseBody.byteStream();
                    }
                })                
                .observeOn(Schedulers.computation())
                .doOnNext(new Action1<InputStream>() {
                    @Override
                    public void call(InputStream inputStream) {
                        try {
                            FileUtils.writeFile(inputStream, FileUtils.getFile(appInfoModel));
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new CustomizeException(e.getMessage(), e);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InputStream>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"onCompleted()");

                    }
                    @Override
                    public void onError(Throwable e) {
                        downloadCompleted();
                        Log.d(TAG,e.toString());
                        Toast.makeText(DownloadFileService.this,DownloadFileService.this.getResources().getString(R.string.downloaded_field),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(InputStream inputStream) {
                        InstallApk.startInstall(DownloadFileService.this,FileUtils.getFile(appInfoModel));
                        DownloadFileService.this.onDestroy();
                    }
                });
    }

    private void downloadCompleted() {
        Log.d(TAG,"downloadCompleted()");
        DownloadModel download = new DownloadModel();
        download.setProgress(100);
        sendIntent(download);
        if(UpdateKey.WITH_NOTIFITION){
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText(getResources().getString(R.string.file_downloaded));
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void sendNotification(DownloadModel download) {
        Log.d(TAG,"sendNotification()");
        sendIntent(download);
        if(UpdateKey.WITH_NOTIFITION) {
            notificationBuilder.setProgress(100, download.getProgress(), false);
            notificationBuilder.setContentText(StringUtils.getDataSize(download.getCurrentFileSize()) + "/" + StringUtils.getDataSize(download.getTotalFileSize()));
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void sendIntent(DownloadModel download) {
        Log.d(TAG,"sendIntent()");
        rxBus.post(ProgressBarActivity.MESSAGE_PROGRESS,download);
    }

    private void coloseDownload(){
        Log.d(TAG,"coloseDownload()");
        rxBus.toObserverableOnMainThread(ProgressBarActivity.MESSAGE_COLOSE, new RxBusResult() {
            @Override
            public void onRxBusResult(Object o) {
                Log.d(TAG,"coloseDownload");
                if(o instanceof String){
                    String msg = (String) o;
                    if(ProgressBarActivity.MESSAGE_COLOSE.equals(msg)){
                        Log.d(TAG,"ondestory");
                        DownloadFileService.this.onDestroy();
                    }
                }
            }
        });
    }
    @Override
    public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
        Log.d(TAG,"onResponseProgress======done====>>"+done);
        DownloadModel download = new DownloadModel();
        download.setTotalFileSize(contentLength);
        download.setCurrentFileSize(bytesRead);
        int progress = (int) ((bytesRead * 100) / contentLength);
        download.setProgress(progress);
        sendNotification(download);
    }

    public static void startDownloadFileService(Context context,AppInfoModel appInfoModel){
        Log.d(TAG,"startDownloadFileService");
        Intent intent = new Intent(context, DownloadFileService.class);
        intent.putExtra(PromptDialogActivity.INTENT_DOWNLOAD_MODEL,appInfoModel);
        context.startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
        destoryVariable();
    }
    
    public void destoryVariable(){
        Log.d(TAG,"destoryVariable()");
        if(subscription!=null&&!subscription.isUnsubscribed()){
            Log.d(TAG,"subscription.isUnsubscribed()");
            subscription.unsubscribe();
        }
        if(rxBus!=null){
            Log.d(TAG,"rxBus.release()");
            rxBus.removeObserverable(ProgressBarActivity.MESSAGE_PROGRESS);
        }
        if(notificationManager!=null){
            Log.d(TAG,"notificationManager.cancel(0)");
            notificationManager.cancel(0);
        }
    }
}