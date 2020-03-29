package com.actspam.api;

import com.actspam.models.DeviceInfo;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeviceInfoApi {
    @POST("/device/adddevice")
    Call<Response> addDeviceInfo(@Body DeviceInfo deviceInfo);
}
