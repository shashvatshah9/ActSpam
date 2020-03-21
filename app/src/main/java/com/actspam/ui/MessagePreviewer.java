package com.actspam.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.actspam.R;

public class MessagePreviewer{
    public void show(Context context, View source, String text) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.message_preview, null);
        TextView textView = dialogView.findViewById(R.id.message_thread_bubble);
        textView.setText(text);
        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.setContentView(dialogView);
        dialog.show();

        source.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (dialog.isShowing()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    int action = event.getActionMasked();
                    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        dialog.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
