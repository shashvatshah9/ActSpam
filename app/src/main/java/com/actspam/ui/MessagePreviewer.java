package com.actspam.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.actspam.R;

public class MessagePreviewer{
    public void show(Context context, View source, String text) {
        BitmapDrawable background = getBlurredScreenDrawable(context, source.getRootView());
        View dialogView = LayoutInflater.from(context).inflate(R.layout.message_preview, null);
        TextView textView = dialogView.findViewById(R.id.message_thread_bubble);
        textView.setText(text);

        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.getWindow().setBackgroundDrawable(background);
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

    public static BitmapDrawable getBlurredScreenDrawable(Context context, View screen) {
        Bitmap screenshot = takeScreenshot(screen);
        Bitmap blurred = blurBitmap(context, screenshot);
        return new BitmapDrawable(context.getResources(), blurred);
    }

    private static Bitmap takeScreenshot(View screen) {
        screen.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screen.getDrawingCache());
        screen.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private static Bitmap blurBitmap(Context context, Bitmap bitmap) {
        float bitmapScale = 0.3f;
        float blurRadius = 10f;

        int width = Math.round(bitmap.getWidth() * bitmapScale);
        int height = Math.round(bitmap.getHeight() * bitmapScale);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(blurRadius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }
}
