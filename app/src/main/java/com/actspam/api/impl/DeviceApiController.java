package com.actspam.api.impl;

import com.actspam.BuildConfig;
import com.actspam.api.DeviceInfoApi;
import com.actspam.models.DeviceInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeviceApiController implements Callback<Response> {

    // TODO : SET THE BASE URL
    static final String BASE_URL = BuildConfig.SERVER_URL;
    private Gson gson;
    private Retrofit retrofit;
    private DeviceInfoApi api;

    public DeviceApiController(){
        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        api = retrofit.create(DeviceInfoApi.class);
    }

    public void startAddDevice(final DeviceInfo deviceInfo){
        Call<Response> call = api.addDeviceInfo(deviceInfo);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Response> call, Response<Response> response) {
        if(response.isSuccessful()){
            System.out.println(response.body());
        }
        else{
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {
        t.printStackTrace();
    }
}
