package com.actspam.utility;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.actspam.models.DeviceMessage;
import com.actspam.models.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsFetchFromDevice {

    private List<DeviceMessage> smsList;
    private Context context;
    private DeviceMessage deviceMessage;
    private Message message;
    private SimpleDateFormat format;


    public SmsFetchFromDevice(Context context){
        this.context = context;
    }

    public List<DeviceMessage> getSMS(){
        smsList = new ArrayList<>();

        Uri smsUri = Uri.parse("content://sms/inbox");
        if(ContextCompat.checkSelfPermission(this.context, "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {

            Cursor cur = context.getContentResolver().query(smsUri, null, null, null, null);
            while (cur != null && cur.moveToNext()) {
                String address = cur.getString(cur.getColumnIndex("address"));
                String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                Date date = new Date(cur.getLong(cur.getColumnIndex("date")));
                Long threadId = cur.getLong(cur.getColumnIndex("thread_id"));
                Long id = cur.getLong(cur.getColumnIndex("_id"));
                boolean hasRead = cur.getInt(cur.getColumnIndex("read")) == 1;
                message = new Message();
                deviceMessage = new DeviceMessage();
                message.setSentBy(address);
                message.setMessageBody(body);
                message.setDate(date);

                deviceMessage.setMessage(message);
                deviceMessage.setId(id);
                deviceMessage.setThreadId(threadId);
                deviceMessage.setHasRead(hasRead);
                smsList.add(deviceMessage);
            }

            if (cur != null) {
                cur.close();
            }
        }
        else{
            Log.i("SMS", "No permission ");
        }
        return smsList;
    }

    public boolean sendToServer(){
        // TODO : PASS MESSAGES TO DAO
        return false;
    }
}
