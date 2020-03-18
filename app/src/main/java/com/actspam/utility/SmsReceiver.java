package com.actspam.utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


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
                            makeToast(context, msg.getOriginatingAddress());
                            // TODO : SEND THE MESSAGE TO THE SERVER
                        }
                        else {
                            msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            smsMessageList.add(msg);
                            // TODO : MAKE A NOTIFICATION AND ABORT OTHER NOTIFICATIONS
                            makeToast(context, msg.getOriginatingAddress());
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

    private void generateInboxStyleNotification() {

//        Log.d("NOTIFICATION", "generateInboxStyleNotification()");
//
//
//        // Main steps for building a INBOX_STYLE notification:
//        //      0. Get your data
//        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
//        //      2. Build the INBOX_STYLE
//        //      3. Set up main Intent for notification
//        //      4. Build and issue the notification
//
//        // 0. Get your data (everything unique per Notification).
//        InboxStyleEmailAppData inboxStyleEmailAppData =
//                getInboxStyleData();
//
//        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
//        String notificationChannelId =
//                NotificationUtil.createNotificationChannel(this, inboxStyleEmailAppData);
//
//        // 2. Build the INBOX_STYLE.
//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
//                // This title is slightly different than regular title, since I know INBOX_STYLE is
//                // available.
//                .setBigContentTitle(inboxStyleEmailAppData.getBigContentTitle())
//                .setSummaryText(inboxStyleEmailAppData.getSummaryText());
//
//        // Add each summary line of the new emails, you can add up to 5.
//        for (String summary : inboxStyleEmailAppData.getIndividualEmailSummary()) {
//            inboxStyle.addLine(summary);
//        }
//
//        // 3. Set up main Intent for notification.
//        Intent mainIntent = new Intent(this, InboxMainActivity.class);
//
//        // When creating your Intent, you need to take into account the back state, i.e., what
//        // happens after your Activity launches and the user presses the back button.
//
//        // There are two options:
//        //      1. Regular activity - You're starting an Activity that's part of the application's
//        //      normal workflow.
//
//        //      2. Special activity - The user only sees this Activity if it's started from a
//        //      notification. In a sense, the Activity extends the notification by providing
//        //      information that would be hard to display in the notification itself.
//
//        // Even though this sample's MainActivity doesn't link to the Activity this Notification
//        // launches directly, i.e., it isn't part of the normal workflow, a eamil app generally
//        // always links to individual emails as part of the app flow, so we will follow option 1.
//
//        // For an example of option 2, check out the BIG_TEXT_STYLE example.
//
//        // For more information, check out our dev article:
//        // https://developer.android.com/training/notify-user/navigation.html
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        // Adds the back stack.
//        stackBuilder.addParentStack(InboxMainActivity.class);
//        // Adds the Intent to the top of the stack.
//        stackBuilder.addNextIntent(mainIntent);
//        // Gets a PendingIntent containing the entire back stack.
//        PendingIntent mainPendingIntent =
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        mainIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        // 4. Build and issue the notification.
//
//        // Because we want this to be a new notification (not updating a previous notification), we
//        // create a new Builder. However, we don't need to update this notification later, so we
//        // will not need to set a global builder for access to the notification later.
//
//        NotificationCompat.Builder notificationCompatBuilder =
//                new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);
//
//        GlobalNotificationBuilder.setNotificationCompatBuilderInstance(notificationCompatBuilder);
//
//        notificationCompatBuilder
//
//                // INBOX_STYLE sets title and content for API 16+ (4.1 and after) when the
//                // notification is expanded.
//                .setStyle(inboxStyle)
//
//                // Title for API <16 (4.0 and below) devices and API 16+ (4.1 and after) when the
//                // notification is collapsed.
//                .setContentTitle(inboxStyleEmailAppData.getContentTitle())
//
//                // Content for API <24 (7.0 and below) devices and API 16+ (4.1 and after) when the
//                // notification is collapsed.
//                .setContentText(inboxStyleEmailAppData.getContentText())
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(
//                        getResources(),
//                        R.drawable.ic_person_black_48dp))
//                .setContentIntent(mainPendingIntent)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                // Set primary color (important for Wear 2.0 Notifications).
//                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
//
//                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
//                // devices and all Wear devices. If you have more than one notification and
//                // you prefer a different summary notification, set a group key and create a
//                // summary notification via
//                // .setGroupSummary(true)
//                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)
//
//                // Sets large number at the right-hand side of the notification for API <24 devices.
//                .setSubText(Integer.toString(inboxStyleEmailAppData.getNumberOfNewEmails()))
//
//                .setCategory(Notification.CATEGORY_EMAIL)
//
//                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
//                // 'importance' which is set in the NotificationChannel. The integers representing
//                // 'priority' are different from 'importance', so make sure you don't mix them.
//                .setPriority(inboxStyleEmailAppData.getPriority())
//
//                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
//                // visibility is set in the NotificationChannel.
//                .setVisibility(inboxStyleEmailAppData.getChannelLockscreenVisibility());
//
//        // If the phone is in "Do not disturb mode, the user will still be notified if
//        // the sender(s) is starred as a favorite.
//        for (String name : inboxStyleEmailAppData.getParticipants()) {
//            notificationCompatBuilder.addPerson(name);
//        }
//
//        Notification notification = notificationCompatBuilder.build();
//
//        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

}
