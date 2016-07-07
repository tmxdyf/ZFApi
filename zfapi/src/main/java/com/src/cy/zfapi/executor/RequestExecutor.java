package com.src.cy.zfapi.executor;

import android.util.Log;

import com.google.gson.Gson;
import com.src.cy.zfapi.ZFApi;
import com.src.cy.zfapi.api.ApiService;
import com.src.cy.zfapi.factory.StringConverterFactory;
import com.src.cy.zfapi.util.DESUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by CY on 2016/3/30.
 */
public class RequestExecutor {


    private static ApiService mApiService;

    static {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                ResponseBody responseBody = response.body();
                String result = responseBody.string();

                result = ZFApi.isEncrypt() ? DESUtil.decryptDoNet(result) : result;//解密
                if (ZFApi.isDebug()) {
                    Log.e("Client", "intercept#result:" + result);
                }
                ResponseBody newResponseBody = ResponseBody.create(responseBody.contentType(), result);
                Response newResponse = response.newBuilder().body(newResponseBody).build();
                return newResponse;
            }
        }).build();

//        初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ZFApi.getWebService())
                .client(client)
                .addConverterFactory(StringConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        mApiService = retrofit.create(ApiService.class);
    }

    private static ApiService getApiService() {
        return mApiService;
    }

    /**
     * 请求网络入口
     *
     * @param method 请求Web的方法体
     * @param object 需要上传的参数与内容,将被转换为json字符串形式
     * @param cla    返回的结果内容反射的类
     * @param <T>    数据实体类
     * @return 返回观察者对象
     */
    public static <T> Observable<T> request(String method, Object object, final Class<T> cla) {
        Observable<String> observable = getApiService().post2(method, transitionObj(object)).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable.map(new Func1<String, T>() {
            @Override
            public T call(String s) {
                return new Gson().fromJson(s, cla);
            }
        });
    }

//    /**
//     * 加密Map
//     *
//     * @return 返回加密后的json-value键值对
//     */
//    private static Map<String, Object> encryptMap(Map<String, Object> map) {
//        String json = new Gson().toJson(map);
//        String encryptContent = DESUtil.encryptAsDoNet(json);
//        if (ZFApi.isDebug()) {
//            Log.e("RequestExecutor", "encryptMap start:" + json);
//            Log.e("RequestExecutor", "encryptMap end:" + encryptContent);
//        }
//        map.clear();
//        map.put("json", encryptContent);
//        return map;
//    }

    /**
     * 转换数据
     *
     * @param object 传入的对象，将被转换为json字符串形式
     * @return 返回加密后的json-value键值对
     */
    private static Map<String, Object> transitionObj(Object object) {

        Map<String, Object> map = new HashMap<>();

        String json = new Gson().toJson(object);
        if (ZFApi.isDebug()) {
            Log.e("RequestExecutor", "请求json字符串:" + json);
        }
        String key = "json";
        if (ZFApi.isEncrypt()) {
            map.put(key, DESUtil.encryptAsDoNet(json));
            if (ZFApi.isDebug()) {
                Log.e("RequestExecutor", "加密后内容:" + map.get(key));
            }
        } else {
            map.put(key, json);
        }


        return map;
    }
}
