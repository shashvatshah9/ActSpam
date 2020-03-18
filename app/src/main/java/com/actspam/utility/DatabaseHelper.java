package com.actspam.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.actspam.models.DeviceMessage;
import com.actspam.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "actspam";
    private static final String GET_MESSAGES_QUERY = "SELECT  * FROM " + AppConstants.MessageTableName + " ORDER BY " +
            AppConstants.DatetimeCol + " DESC";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DeviceMessage.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AppConstants.MessageTableName);
        // Create tables again
        onCreate(db);
    }

    public void insertMessages(List<DeviceMessage> deviceMessages) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        SetMessageTask task = new SetMessageTask(db);
        task.doInBackground(deviceMessages);
    }

    public List<DeviceMessage> getMessages(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(GET_MESSAGES_QUERY, null);
        GetMessageTask task = new GetMessageTask();
        List<DeviceMessage> list = task.doInBackground(cursor);
        db.close();
        return list;
    }

    public int deleteMessages(List<Integer> messageIds){
        SQLiteDatabase db = this.getWritableDatabase();
        String ids[] = new String[messageIds.size()];
        int x=0;
        for(int i : messageIds){
            ids[x++] = Integer.toString(i);
        }
        String whereClause = String.format(AppConstants.IdCol + " in (%s)", TextUtils.join(",", Collections.nCopies(ids.length, "?")));

        return db.delete(AppConstants.MessageTableName, whereClause, ids);
    }

    private class SetMessageTask extends AsyncTask<List<DeviceMessage>, Void, Void>{
        SQLiteDatabase db = null;

        protected SetMessageTask(SQLiteDatabase db){
            this.db = db;
        }

        @Override
        protected Void doInBackground(List<DeviceMessage>... deviceMessages) {
            ContentValues values;
            for (DeviceMessage deviceMessage : deviceMessages[0]) {
                values = new ContentValues();
                values.put(AppConstants.IdCol, deviceMessage.getId());
                values.put(AppConstants.ThreadIdCol, deviceMessage.getThreadId());
                values.put(AppConstants.DatetimeCol, deviceMessage.getMessage().getDate().toString());
                String hasRead = "";
                if (deviceMessage.isHasRead()) {
                    hasRead = "TRUE";
                } else hasRead = "FALSE";
                values.put(AppConstants.HasReadCol, hasRead);
                values.put(AppConstants.LabelCol, deviceMessage.getMessage().getLabel());
                values.put(AppConstants.MessageBodyCol, deviceMessage.getMessage().getMessageBody());
                values.put(AppConstants.SentByCol, deviceMessage.getMessage().getSentBy());
                // insert row
                long id = this.db.insert(AppConstants.MessageTableName, null, values);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.db.close();
        }
    }

    private class GetMessageTask extends AsyncTask<Cursor, Void, List<DeviceMessage>>{
        @Override
        protected List<DeviceMessage> doInBackground(Cursor... cursors) {
            List<DeviceMessage> listMessage = new ArrayList<>();
            if (cursors[0].moveToFirst()) {
                do {
                    DeviceMessage deviceMessage = new DeviceMessage();
                    Message message = new Message();
                    deviceMessage.setId(cursors[0].getLong(cursors[0].getColumnIndex(AppConstants.IdCol)));
                    deviceMessage.setThreadId(cursors[0].getLong(cursors[0].getColumnIndex(AppConstants.ThreadIdCol)));
                    String hasReadString = cursors[0].getString(cursors[0].getColumnIndex(AppConstants.HasReadCol));
                    boolean hasRead = hasReadString.equals("TRUE") ? true: false;
                    deviceMessage.setHasRead(hasRead);
                    Date date = new Date(cursors[0].getLong(cursors[0].getColumnIndex("date")));
                    message.setDate(date);
                    message.setMessageBody(cursors[0].getString(cursors[0].getColumnIndex(AppConstants.MessageBodyCol)));
                    message.setLabel(cursors[0].getString(cursors[0].getColumnIndex(AppConstants.LabelCol)));
                    message.setSentBy(cursors[0].getString(cursors[0].getColumnIndex(AppConstants.SentByCol)));
                    deviceMessage.setMessage(message);
                    listMessage.add(deviceMessage);
                } while (cursors[0].moveToNext());
            }
            return listMessage;
        }
    }
}
