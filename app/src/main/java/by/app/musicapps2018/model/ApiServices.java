package by.app.musicapps2018.model;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServices {

    Retrofit retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://vk.com/")
            .build();

    public IMusicApi getApi(){

        IMusicApi api = retrofit.create(IMusicApi.class);
        return api;
    }
}
