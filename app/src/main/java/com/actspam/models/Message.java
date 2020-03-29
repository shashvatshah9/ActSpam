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
    @SerializedName("label")
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

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
