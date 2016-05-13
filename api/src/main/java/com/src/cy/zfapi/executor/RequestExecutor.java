package com.src.cy.zfapi.executor;

import android.util.Log;

import com.google.gson.Gson;
import com.src.cy.zfapi.ZFApi;
import com.src.cy.zfapi.api.ApiService;
import com.src.cy.zfapi.factory.StringConverterFactory;
import com.src.cy.zfapi.util.DESUtil;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by CY on 2016/3/30.
 */
public class RequestExecutor {


    private static ApiService mApiService;

    static {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Response response = chain.proceed(chain.request());
            ResponseBody responseBody = response.body();
            String result = responseBody.string();

            result = ZFApi.isEncrypt() ? DESUtil.decryptDoNet(result) : result;//解密
            Log.e("Client", "intercept#result:" + result);
            ResponseBody newResponseBody = ResponseBody.create(responseBody.contentType(), result);
            Response newResponse = response.newBuilder().body(newResponseBody).build();
            return newResponse;
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
     * @param method
     * @param map
     * @param cla
     * @param <T>
     * @return
     */
    public static <T> Observable<T> request(String method, Map<String, String> map, Class<T> cla) {
        Observable<String> observable = getApiService().post2(method, ZFApi.isEncrypt() ? encryptMap(map) : map).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable.map(s -> new Gson().fromJson(s, cla));
    }

    /**
     * 加密Map
     *
     * @return
     */
    private static Map<String, String> encryptMap(Map<String, String> map) {
        String json = new Gson().toJson(map);
        String encryptContent = DESUtil.encryptAsDoNet(json);
        map.clear();
        Log.e("RequestHelper", "encryptMap start:" + json);
        map.put("json", encryptContent);
        Log.e("RequestHelper", "encryptMap end:" + encryptContent);
        return map;
    }
}
