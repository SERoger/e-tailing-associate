package com.crow.supply_chain_associate.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static final String BASE_URL="http://192.168.1.172:8086/";
    private static APIClient apiClient;
    private static Retrofit reprofit;

    public APIClient() {
        reprofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized APIClient getInstance(){
        if(apiClient==null){
            apiClient=new APIClient();
        }
        return apiClient;
    }

    public ApiInterface getApi(){
        return reprofit.create(ApiInterface.class);
    }

}