package com.trycath.myupdateapklibrary.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.trycath.myupdateapklibrary.R;
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

public class DownloadService extends IntentService implements ProgressResponseListener{
    private static final String TAG = "DownloadService";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private Subscription subscription;
    private RxBus rxBus = RxBus.getInstance();
    private AppInfoModel appInfoModel;
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"onHandleIntent");
        appInfoModel = (AppInfoModel) intent.getExtras().getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.file_downloading))
                .setSmallIcon(GetAppInfo.getAppIconId(this))
                .setContentText(getResources().getString(R.string.file_downloading))
                .setTicker(getResources().getString(R.string.start_downloaded))
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        coloseDownload();
        download();
    }

    private void download() {
        Log.d(TAG,"download");
        ProgressBarActivity.startActivity(this,appInfoModel);
        DownloadServiceApi downloadService = ServiceGenerator.createResponseService(DownloadServiceApi.class, this);
        subscription = downloadService.download(appInfoModel.getInstallUrl())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ResponseBody, InputStream>() {
                    @Override
                    public InputStream call(ResponseBody responseBody) {
                        return responseBody.byteStream();
                    }
                })
                .unsubscribeOn(AndroidSchedulers.mainThread())
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
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InputStream>() {
                    @Override
                    public void onCompleted() {
                        downloadCompleted();
                    }
    
                    @Override
                    public void onError(Throwable e) {
                        downloadCompleted();
                        Log.d(TAG,e.toString());
                    }
    
                    @Override
                    public void onNext(InputStream inputStream) {
    
                    }
                });
    }

    private void downloadCompleted() {
        DownloadModel download = new DownloadModel();
        download.setProgress(100);
        sendIntent(download);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(getResources().getString(R.string.file_downloaded));
        notificationManager.notify(0, notificationBuilder.build());
        Log.d(TAG,"install apk............");
        InstallApk.startInstall(DownloadService.this,FileUtils.getFile(appInfoModel));
        
    }

    private void sendNotification(DownloadModel download) {
        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(StringUtils.getDataSize(download.getCurrentFileSize()) + "/" + StringUtils.getDataSize(download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendIntent(DownloadModel download) {
        rxBus.post(ProgressBarActivity.MESSAGE_PROGRESS,download);
    }
    
    private void coloseDownload(){
        rxBus.toObserverableOnMainThread(ProgressBarActivity.MESSAGE_COLOSE, new RxBusResult() {
            @Override
            public void onRxBusResult(Object o) {
                Log.d(TAG,"coloseDownload");
                if(o instanceof String){
                    String msg = (String) o;
                    if(ProgressBarActivity.MESSAGE_COLOSE.equals(msg)){
                        Log.d(TAG,"ondestory");
                        DownloadService.this.onDestroy();
                    }
                }
            }
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG,"onTaskRemoved");
        notificationManager.cancel(0);
        if(subscription!=null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
        if(rxBus!=null){
            rxBus.release();
        }
    }
    
    public static void startDownloadService(Context context,AppInfoModel appInfoModel){
        Log.d(TAG,"startDownloadService");
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(PromptDialogActivity.INTENT_DOWNLOAD_MODEL,appInfoModel);
        context.startService(intent);
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

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        if(subscription!=null){
            Log.d(TAG,"subscription.isUnsubscribed()");
            subscription.unsubscribe();
        }
        if(rxBus!=null){
            Log.d(TAG,"rxBus.release()");
            rxBus.release();
        }
        if(notificationManager!=null){
            Log.d(TAG,"notificationManager.cancel(0)");
            notificationManager.cancel(0);
        }
    }
}
