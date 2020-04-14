package com.actspam.utility;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.actspam.ui.notification.MessageNotificationBuilder;


public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        SmsMessage msg;
                        for (int i = 0; i < pdus.length; i++) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                String format = bundle.getString("format");
                                msg = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                                // TODO : MAKE A NOTIFICATION AND ABORT OTHER NOTIFICATIONS
                                makeToast(context, msg.getOriginatingAddress());
                                // TODO : SEND THE MESSAGE TO THE SERVER
                            } else {
                                msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                // TODO : MAKE A NOTIFICATION AND ABORT OTHER NOTIFICATIONS
                                makeToast(context, msg.getOriginatingAddress());
                                // TODO : SEND THE MESSAGE TO THE SERVER
                            }
                            // TODO : SYNC WITH LOCAL DB
                            Log.i("notif", "to call notification");
                            MessageNotificationBuilder.generateNotification(context, msg.getMessageBody());
                        }
                    } catch (Exception e) {
                        Log.d("Exception caught", e.getMessage());
                    }
                }
        }
    }

    private void makeToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
