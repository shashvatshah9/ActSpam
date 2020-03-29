package com.actspam.api;

import com.actspam.models.Message;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MessageApi {
    @POST("/message/addmessage")
    Call<Response> sendMessage(@Body Message message);
}
