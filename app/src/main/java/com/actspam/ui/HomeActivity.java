package com.actspam.ui;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.actspam.R;
import com.actspam.classifier.ClassifyText;
import com.actspam.models.DeviceInfo;
import com.actspam.models.DeviceMessage;
import com.actspam.ui.adapter.MessageAdapter;
import com.actspam.ui.adapter.SwipeToDeleteCallback;
import com.actspam.utility.DatabaseHelper;
import com.actspam.utility.SmsFetchFromDevice;
import com.actspam.utility.SmsReceiver;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    final private int PERMISSION_REQUEST_CODE = 0;
    final String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS};

    public static final String DevicePreferences = "DEVICE_INFO";
    public static final String MessagePreferences = "MESSAGES";

    private ClassifyText classifyText;
    private Handler handler;
    private DeviceInfo deviceInfo;
    private SmsFetchFromDevice fetchSms;
    private SmsReceiver smsReceiver;
    private List<DeviceMessage> smsList;
    private DatabaseHelper db;

    private TextView textView;
    private View mLayout;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.i("Demo", "OnCreate");
        mLayout = findViewById(R.id.main_activity_relative_layout);
        recyclerView = findViewById(R.id.message_recycler_view);
        layoutManager = new LinearLayoutManager(this);

        classifyText = new ClassifyText(this);
        handler = new Handler();
        checkPermissions();

        //        smsReceiver = new SmsReceiver();

        Log.i("SMS","sms loaded");
        messageAdapter = new MessageAdapter(getApplicationContext(), smsList, this);

        setUpRecyclerView();

//        for(final Message sms: smsList){
//            String smsBody = sms.getSentBy() + " " + sms.getMessageBody();
//            AsyncTask.execute(()-> handler.post(()->callWorker(smsBody)));
//        }

    }

    /**
     * Check the required permissions on the device
     */
    private void checkPermissions() {
        // required permissions are not present
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
        else{
            // permissions are granted to the application
            // setUpDevice() and fetch the sms from device
            setUpDevice();
        }
    }

    /**
     * Fetch the device infomation and load the sms from ContextProvider or local database
     */
    private void setUpDevice(){
        // check shared preferences if device info is present or not
        SharedPreferences devicePreferences = getSharedPreferences(DevicePreferences, Context.MODE_PRIVATE);
        db = new DatabaseHelper(this);

        if(devicePreferences.getAll().size() == 0){
            deviceInfo = new DeviceInfo(this);
            Log.i("device info", deviceInfo.toString());
            // TODO : SEND DEVICE INFO TO SERVER
            // TODO : SAVE DEVICE INFO IN LOCALLY
        }
             Map<String, String> devicePrefencesMap = (Map<String, String>) devicePreferences.getAll();
            deviceInfo = new DeviceInfo(this, devicePrefencesMap);
        }

        SharedPreferences messagePreferences = getSharedPreferences(MessagePreferences, Context.MODE_PRIVATE);
        boolean isMessageDbSet = messagePreferences.getBoolean("DB_SET", false);
        if(isMessageDbSet){
            // fetch the messages from the local database
            smsList.addAll(db.getMessages());
        }
        else{
            // fetch the message from the device and put it to local database
            fetchSms = new SmsFetchFromDevice(this);
            smsList = fetchSms.getSMS();
            db.insertMessages(smsList);
        }
    }


    private void setUpRecyclerView(){
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(messageAdapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(getApplicationContext(), messageAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @WorkerThread
    protected void callWorker(String sms){
        handler.post(()->{
            if(sms!=null) {
                String result = classifyText.classify(sms);
                Log.i("output", result);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setUpDevice();
            } else if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setUpDevice();
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, R.string.phonestate_permission_denied,
                        Snackbar.LENGTH_INDEFINITE).setAction(R.string.give_permission, (View view) -> {
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                }).show();
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        handler.post(() -> classifyText.unload());
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(() -> classifyText.load());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
