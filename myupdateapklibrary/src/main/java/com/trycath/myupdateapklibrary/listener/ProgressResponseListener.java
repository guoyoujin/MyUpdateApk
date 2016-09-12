package com.trycath.myupdateapklibrary.listener;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-12 14:49
 * @version: V1.0 <描述当前版本功能>
 */

public interface ProgressResponseListener {
    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}