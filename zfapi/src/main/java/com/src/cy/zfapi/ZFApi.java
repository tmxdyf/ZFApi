package com.src.cy.zfapi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.src.cy.zfapi.executor.RequestExecutor;

import java.lang.reflect.Type;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by CY on 2016/3/30.
 */
public class ZFApi {

    private static String WEB_SERVICE = null;
    private static boolean isEncrypt = false;
    private static String desKey = null;

    /**
     * 初始化请求API
     *
     * @param webServiceUrl 请求URL
     * @param isEncrypt     是否加密
     * @param desKey        加密密钥
     */
    public static void init(String webServiceUrl, boolean isEncrypt, String desKey) {
        ZFApi.WEB_SERVICE = webServiceUrl;
        ZFApi.isEncrypt = isEncrypt;
        ZFApi.desKey = desKey;
    }

    /**
     * 方式一 返回观察者对象，需外部订阅并添加至订阅容器中
     * 请求网络入口,结果在主线程返回
     *
     * @param method 请求Web的方法体
     * @param map 需要上传的参数与内容
     * @param cla 返回的结果内容反射的类
     * @param <T>    数据实体类
     * @return 返回观察者对象
     */
    public static <T> Observable<T> request(String method, Map<String, Object> map, Class<T> cla) {
        return RequestExecutor.request(method, map, cla);
    }

    /**
     * 方式二 数据在Subscriber返回，不返回观察者对象
     *
     * @param method 请求Web的方法体
     * @param map 需要上传的参数与内容
     * @param cla 返回的结果内容反射的类
     * @param subscriber 订阅实体类
     * @param compositeSubscription 订阅成功后将返回的Subscription装入compositeSubscription中
     * @param <T> 数据实体类
     */
    public static <T> void request(String method, Map<String, Object> map, Class<T> cla, Subscriber<T> subscriber, CompositeSubscription compositeSubscription) {
        Observable<T> observable = ZFApi.request(method, map, cla);
        Subscription subscription = observable.subscribe(subscriber);
        compositeSubscription.add(subscription);
    }

    public static Map<String, Object> objToMap(Object entity) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(entity), type);
    }


    public static String getWebService() {
        return WEB_SERVICE;
    }

    public static boolean isEncrypt() {
        return isEncrypt;
    }

    public static String getDesKey() {
        return desKey;
    }
}
