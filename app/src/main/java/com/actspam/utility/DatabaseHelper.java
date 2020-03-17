package com.actspam.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.actspam.models.DeviceMessage;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "actspam";


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
        ContentValues values;
        for(DeviceMessage deviceMessage : deviceMessages) {
            values = new ContentValues();
            values.put(AppConstants.IdCol, deviceMessage.getId());
            values.put(AppConstants.ThreadIdCol, deviceMessage.getThreadId());
            values.put(AppConstants.DatetimeCol, deviceMessage.getMessage().getDate().toString());
            values.put(AppConstants.HasReadCol, deviceMessage.isHasRead());
            values.put(AppConstants.LabelCol, deviceMessage.getMessage().getLabel());
            values.put(AppConstants.MessageBodyCol, deviceMessage.getMessage().getMessageBody());
            values.put(AppConstants.SentByCol, deviceMessage.getMessage().getSentBy());
            // insert row
            long id = db.insert(AppConstants.MessageTableName, null, values);
        }
        // close db connection
        db.close();
    }
}
