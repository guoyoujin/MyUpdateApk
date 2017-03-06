package com.trycath.myupdateapklibrary.rxbus;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-13 10:08
 * @version: V1.0 <描述当前版本功能>
 */

public class RxBus {
    private static final String TAG = "RxBus";
    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());
    private final Map<String, Object> tags = new HashMap<>(); //post队列
    private final Map<String, Object> sendtags = new HashMap<>(); //结果发送队列

    private static RxBus rxbus;

    public static RxBus getInstance()
    {
        if(rxbus == null)
        {
            synchronized (RxBus.class) {
                if(rxbus == null) {
                    rxbus = new RxBus();
                }
            }
        }
        return rxbus;
    }

    /**
     * 发送事件消息
     * @param tag 用于区分事件
     * @param object 事件的参数
     */
    public void post(String tag, Object object)
    {
        if(!tags.containsKey(tag))
        {
            tags.put(tag, object);
            _bus.onNext(object);
            sendtags.put(tag, object);
        }
        else
        {
            tags.remove(tag);
            tags.put(tag, object);
            _bus.onNext(object);
            sendtags.put(tag, object);
        }
    }

    /**
     * 主线程中执行
     * @param tag
     * @param rxBusResult
     */
    public void toObserverableOnMainThread(final String tag, final RxBusResult rxBusResult) {

        _bus.onBackpressureDrop().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (sendtags.containsKey(tag)) {
                    rxBusResult.onRxBusResult(sendtags.get(tag));
                    //sendtags.remove(tag);
                }
            }
        });
    }

    /**
     * 子线程中执行
     * @param tag
     * @param rxBusResult
     */
    public void toObserverableChildThread(final String tag, final RxBusResult rxBusResult) {

        _bus.onBackpressureDrop().observeOn(Schedulers.io()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (sendtags.containsKey(tag)) {
                    rxBusResult.onRxBusResult(sendtags.get(tag));
                    //sendtags.remove(tag);
                }
            }
        });
    }

    /**
     * 移除tag
     * @param tag
     */
    public void removeObserverable(String tag)
    {
        if(tags.containsKey(tag))
        {
            tags.remove(tag);
        }
        if(sendtags.containsKey(tag))
        {
            sendtags.remove(tag);
        }
    }

    /**
     * 退出应用时，清空资源
     */
    public void release()
    {
        tags.clear();
        sendtags.clear();
        rxbus = null;
    }
}