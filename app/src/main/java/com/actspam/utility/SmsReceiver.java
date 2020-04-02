package com.actspam.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.actspam.ui.notification.MessageNotificationBuilder;


public class  SmsReceiver extends BroadcastReceiver {

    @SuppressWarnings(value = "deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();

            if(bundle!=null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage msg;
                    for(int i=0; i < pdus.length; i++){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = bundle.getString("format");
                            msg = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                            // TODO : MAKE A NOTIFICATION AND ABORT OTHER NOTIFICATIONS
                            makeToast(context, msg.getOriginatingAddress());
                            // TODO : SEND THE MESSAGE TO THE SERVER
                        }
                        else {
                            msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            // TODO : MAKE A NOTIFICATION AND ABORT OTHER NOTIFICATIONS
                            makeToast(context, msg.getOriginatingAddress());
                            // TODO : SEND THE MESSAGE TO THE SERVER
                        }
                        // TODO : SYNC WITH LOCAL DB
                        MessageNotificationBuilder.generateNotification(context, msg.getDisplayMessageBody());
                    }
                }catch(Exception e){
                    Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    private void makeToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
