package com.actspam.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.actspam.R;
import com.actspam.models.DeviceMessage;
import com.actspam.models.Message;
import com.actspam.ui.HomeActivity;
import com.actspam.ui.MessagePreviewer;
import com.actspam.ui.MessageViewActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private HomeActivity mainActivity;
    private List<DeviceMessage> messageList;
    private static final String datePattern = "dd-MM-yyyy hh:mm";
    private DateFormat format;
    private boolean multiSelect = false;
    private ArrayList<Integer> selectedMessages;
    private DeviceMessage recentlyDeletedMessage;
    private int recentlyDeletedMessagePosition = -1;

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            menu.add("Delete");
            return true ;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for(Integer pos : selectedMessages){
                // if does not work, remove by index
                selectedMessages.remove(pos);
                deleteTask(pos);
                notifyItemRemoved(pos);
                // TODO : UPDATE THE CLASSIFICATION DATA / LABELLING DATA
            }
            Toast.makeText(context,"Messages Deleted",Toast.LENGTH_SHORT).show();

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedMessages.clear();
            notifyDataSetChanged();
        }
    };

    public MessageAdapter(Context context, List<DeviceMessage> messageList, HomeActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.messageList = messageList;
        this.selectedMessages = new ArrayList<>();
        recentlyDeletedMessage = new DeviceMessage();
        this.format = DateFormat.getDateInstance(DateFormat.SHORT);
    }

    // only for swipe action
    public void deleteTask(int position){
        recentlyDeletedMessage = messageList.get(position);
        Long id = recentlyDeletedMessage.getId();
        Long thread_id = recentlyDeletedMessage.getThreadId();
        Uri uri = Uri.parse("content://sms/inbox/" + String.valueOf(id));
        recentlyDeletedMessagePosition = position;
        messageList.remove(position);
        notifyItemRemoved(position);
        try {
            Cursor cur = context.getContentResolver().query(uri, null, null, null, null);
            if (cur.moveToFirst()){
                Log.i("Rows", String.valueOf(cur.getLong(0)));
                int rowsDeleted = context.getContentResolver().delete(uri, null, null);
                Log.i("Rows", Integer.toString(rowsDeleted));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceMessage deviceMessage = messageList.get(position);
        Message message = deviceMessage.getMessage();
        holder.dateView.setText(format.format(message.getDate()));
        holder.senderNameView.setText(message.getSentBy());
        holder.senderTextView.setText(message.getMessageBody().trim());
        holder.roundNameTextView.setText(message.getSentBy().substring(0,2));
        if(deviceMessage.isHasRead()){
            holder.unreadCount.setVisibility(View.GONE);
        }
        else holder.unreadCount.setVisibility(View.VISIBLE);
        if(message.getLabel().equals("SPAM")){
            holder.spamSymbol.setVisibility(View.VISIBLE);
        }
        else holder.spamSymbol.setVisibility(View.GONE);
        holder.update(position);
    }

    @Override
    public int getItemCount() {
        if(messageList!=null)
            return messageList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dateView, senderTextView, senderNameView, roundNameTextView, spamSymbol, unreadCount;
        public View view;

        public ViewHolder(View v){
            super(v);
            view = v;
            dateView = v.findViewById(R.id.date_text);
            senderTextView = v.findViewById(R.id.sender_text);
            senderNameView = v.findViewById(R.id.sender_name);
            roundNameTextView = v.findViewById(R.id.round_name_icon);
            spamSymbol = v.findViewById(R.id.spam_symbol);
            unreadCount = v.findViewById(R.id.unread_count);
        }

        void selectItem(final int position) {
            if (multiSelect) {
                if (selectedMessages.contains(position)) {
                    selectedMessages.remove(selectedMessages.indexOf(position));
                    view.setBackgroundColor(Color.WHITE);
                } else {
                    selectedMessages.add(position);
                    view.setBackgroundColor(Color.RED);
                }
            }
        }

        void update(final int position) {
            if (selectedMessages.contains(position)) {
                view.setBackgroundColor(Color.RED);
            } else {
                view.setBackgroundColor(Color.WHITE);
            }
            view.setOnLongClickListener((View view1)->{
                ((AppCompatActivity)view1.getContext()).startSupportActionMode(actionModeCallback);
                selectItem(position);
                return true;
            });
            view.setOnClickListener((View view1)->{
                selectItem(position);

//                if(multiSelect) {
//                }
//                else{
//                     TODO : OPEN THE THREAD VIEW ACTIVITY
//                }
//                Intent openActivity = new Intent(context, MessageViewActivity.class);
//                openActivity.putExtra("key", value); //Optional parameters
//                mainActivity.startActivity(openActivity);
                new MessagePreviewer().show(view1.getContext(), view, senderTextView.getText().toString());
            });
        }

    }

    public Context getContext(){
        return context;
    }
}
