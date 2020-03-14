package com.actspam.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Message {

    // added serialization to support json data
    @SerializedName("message_body")
    private String messageBody;
    @SerializedName("sender")
    private String sentBy;
    @SerializedName("datetime")
    private Date date;
    @SerializedName("device_id")
    private String deviceId;

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
