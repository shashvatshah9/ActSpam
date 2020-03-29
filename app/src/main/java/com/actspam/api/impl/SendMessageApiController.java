package com.actspam.api.impl;

import com.actspam.BuildConfig;
import com.actspam.api.MessageApi;
import com.actspam.models.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendMessageApiController implements Callback<Response> {
    // TODO : SET THE BASE URL
    static final String BASE_URL = BuildConfig.SERVER_URL;
    private Gson gson;
    private Retrofit retrofit;
    private MessageApi api;

    public SendMessageApiController(){
        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        api = retrofit.create(MessageApi.class);
    }

    public void startSendMessage(final Message message){
        Call<Response> call = api.sendMessage(message);
        call.enqueue(this);
    }

    public void startSendSpamMessage(final Message message){
        Call<Response> call = api.sendMessage(message);
        call.enqueue(this);
    }

    // TODO : MAKE THE LOGIC TO STORE WHICH MESSAGES HAVE BEEN SENT TO THE SERVER
    // SOME SORT OF SYNCING MECHANISM
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
