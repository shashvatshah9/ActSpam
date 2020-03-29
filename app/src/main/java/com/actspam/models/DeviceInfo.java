package com.actspam.models;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class DeviceInfo {

    public class Device {
        @SerializedName("imei")
        private String imei;
        @SerializedName("imsi")
        private String imsi;
        @SerializedName("sdk")
        private String sdk;
        @SerializedName("model")
        private String model;
        @SerializedName("product")
        private String product;
        @SerializedName("network_op")
        private String networkOp;
        @SerializedName("device_id")
        private String deviceId = "";
        @SerializedName("number")
        private String number;

        String getNumber() {
            return number;
        }

        void setNumber(String number) {
            this.number = number;
        }


        String getNetworkOp() {
            return networkOp;
        }

        void setNetworkOp(String networkOp) {
            this.networkOp = networkOp;
        }

        String getImei() {
            return imei;
        }

        void setImei(String imei) {
            this.imei = imei;
        }

        String getImsi() {
            return imsi;
        }

        void setImsi(String imsi) {
            this.imsi = imsi;
        }

        String getSdk() {
            return sdk;
        }

        void setSdk(String sdk) {
            this.sdk = sdk;
        }

        String getModel() {
            return model;
        }

        public String getDeviceId() {
            return deviceId;
        }

        void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        void setModel(String model) {
            this.model = model;
        }

        String getProduct() {
            return product;
        }

        void setProduct(String product) {
            this.product = product;
        }

    }
    private SharedPreferences.Editor devicePreferences;
    private Device currDevice;
    private Context context;

    public static final String DevicePreferences = "DEVICE_INFO";

    public DeviceInfo(Context context) {
        this.context = context;
        setThisDevice();
    }

    public DeviceInfo(Context context, Map<String, String> devicePreferenceMap){
        this.context = context;
        if(devicePreferenceMap!=null) {
            setDeviceFromMap(devicePreferenceMap);
        }
        else{
            setThisDevice();
        }
    }

    private void setDeviceFromMap(Map<String, String> preferencesMap){
        Device device = new Device();
        device.setImei(preferencesMap.get("imei"));
        device.setImsi(preferencesMap.get("imsi"));
        device.setDeviceId(preferencesMap.get("deviceId"));
        device.setModel(preferencesMap.get("model"));
        device.setNetworkOp(preferencesMap.get("network_op"));
        device.setNumber(preferencesMap.get("number"));
        device.setSdk(preferencesMap.get("sdk"));
        device.setProduct(preferencesMap.get("product"));
        this.currDevice = device;
    }

    private void setDevicePreferences(){
        if(currDevice!=null){
            devicePreferences = context.getSharedPreferences(DevicePreferences, Context.MODE_PRIVATE).edit();
            devicePreferences.putString("imei", currDevice.getImei());
            devicePreferences.putString("imsi", currDevice.getImsi());
            devicePreferences.putString("deviceId", currDevice.getDeviceId());
            devicePreferences.putString("model", currDevice.getModel());
            devicePreferences.putString("network_op", currDevice.getNetworkOp());
            devicePreferences.putString("number", currDevice.getNumber());
            devicePreferences.putString("sdk", currDevice.getSdk());
            devicePreferences.putString("product", currDevice.getProduct());
            devicePreferences.apply();
        }
    }

    Device getInfoFromDevice() {
        Device d = new Device();
        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager teleManager = (TelephonyManager) context.getSystemService(serviceName);

        String permission = Manifest.permission.READ_PHONE_STATE;
        int res = context.checkCallingOrSelfPermission(permission);

        if(res == PackageManager.PERMISSION_GRANTED){
            d.setImei(teleManager.getDeviceId());
            d.setImsi(teleManager.getSubscriberId());
            d.setNumber(teleManager.getLine1Number());
        }
        d.setNetworkOp(teleManager.getNetworkOperator());
        d.setModel(Build.MODEL);
        d.setProduct(Build.PRODUCT);
        d.setSdk(Build.VERSION.RELEASE);
        return d;
    }

    public Device getCurrDevice() {
        return currDevice;
    }

    void setThisDevice() {
        this.currDevice = getInfoFromDevice();
        // add these values to the sharePreferences
        setDevicePreferences();
    }

    // TODO : when a device is registered, it's id is returned by the server on response
    public void setThisDeviceId(String deviceId){
        currDevice.setDeviceId(deviceId);
    }

    @NonNull
    @Override
    public String toString() {

        if(currDevice !=null){
            return  currDevice.getImei() + "," + currDevice.getImsi() + "," + currDevice.getModel() + "," + currDevice.getSdk() + ","
                    + currDevice.getNetworkOp() + "," + currDevice.getProduct() + "," + currDevice.getNumber();
        }
        return "";
    }
}
