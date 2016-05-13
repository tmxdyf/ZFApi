package com.src.cy.zfapi.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by CY on 2016/3/16.
 */
public interface ApiService {


    @FormUrlEncoded
    @POST("{method}")
    Call<String> post(@Path("method") String method, @FieldMap Map<String, String> fieldMap);


    @FormUrlEncoded
    @POST("{method}")
    Observable<String> post2(@Path("method") String method, @FieldMap Map<String, String> fieldMap);


}
