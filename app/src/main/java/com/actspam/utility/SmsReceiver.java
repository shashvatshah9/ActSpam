package com.actspam.utility;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.actspam.R;
import com.actspam.ui.HomeActivity;

import java.util.ArrayList;
import java.util.List;


public class SmsReceiver extends BroadcastReceiver {

    @SuppressWarnings(value = "deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            List<SmsMessage> smsMessageList;

            if(bundle!=null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    smsMessageList = new ArrayList<>();
                    SmsMessage msg;
                    for(int i=0; i < pdus.length; i++){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = bundle.getString("format");
                            msg = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                            smsMessageList.add(msg);
                            // TODO : MAKE A NOTIFICATION AND ABORT OTHER NOTIFICATIONS
//                            makeToast(context, msg.getOriginatingAddress());
                            generateNotification(context, msg.getDisplayMessageBody());
                            // TODO : SEND THE MESSAGE TO THE SERVER
                        }
                        else {
                            msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            smsMessageList.add(msg);
                            // TODO : MAKE A NOTIFICATION AND ABORT OTHER NOTIFICATIONS
//                            makeToast(context, msg.getOriginatingAddress());
                            generateNotification(context, msg.getDisplayMessageBody());
                            // TODO : SEND THE MESSAGE TO THE SERVER
                        }
                        String msg_from = msg.getOriginatingAddress();
                        String msgBody = msg.getMessageBody();
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



    private void generateNotification(Context context, String messageText) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Message")
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}
