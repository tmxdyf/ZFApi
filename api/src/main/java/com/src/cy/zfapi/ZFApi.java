package com.src.cy.zfapi;

import com.src.cy.zfapi.executor.RequestExecutor;

import java.util.Map;

import rx.Observable;

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
     * 请求网络入口,结果在主线程返回
     *
     * @param method
     * @param map
     * @param cla
     * @param <T>    数据实体类
     * @return
     */
    public static <T> Observable<T> request(String method, Map<String, String> map, Class<T> cla) {
        return RequestExecutor.request(method, map, cla);
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
