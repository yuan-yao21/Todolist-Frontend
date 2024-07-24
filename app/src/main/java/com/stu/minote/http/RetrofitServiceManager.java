package com.stu.minote.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 90000;
    private static final int DEFAULT_WRITE_TIME = 90000;
    private static final int DEFAULT_READ_TIME = 90000;
    private final OkHttpClient okHttpClient;
//    public static String REQUEST_PATH = "http://101.132.45.198:8000/";
    public static String REQUEST_PATH = "http://116.198.232.203:8000/";
    public static String IMAGE_HEAD = "http://116.198.232.203:8000";
    private final Retrofit retrofit;

    private RetrofitServiceManager() {

        HttpLoggingInterceptor LoginInterceptor = new HttpLoggingInterceptor();
        LoginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)//设置写操作超时时间
                .addInterceptor(LoginInterceptor)
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)//设置读操作超时时间
                .build();


        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(REQUEST_PATH)//设置服务器路径
                .addConverterFactory(GsonConverterFactory.create())//添加转化库，默认是Gson
//                .addConverterFactory(StringConverterFactory.create())//添加转化库，默认是Gson
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加回调库，采用RxJava
                .build();

    }

//    private static class SingletonHolder {
//        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
//    }

    static RetrofitServiceManager retrofitServiceManager;

    /*
     * 获取RetrofitServiceManager
     **/
    public static RetrofitServiceManager getInstance() {

        if (retrofitServiceManager == null) {
            retrofitServiceManager = new RetrofitServiceManager();
        }


        return retrofitServiceManager;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
