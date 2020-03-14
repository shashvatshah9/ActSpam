package com.actspam.classifier;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import androidx.annotation.WorkerThread;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ClassifyText {
    private String modelPath = "spam.tflite";
    private Interpreter interpreter;
    private MappedByteBuffer buffer;
    private Context context;
    private ClassfierUtility utility;

    public ClassifyText(Context context){
        this.context = context;
        this.utility = new ClassfierUtility(this.context);
    }

    @WorkerThread
    public synchronized void load() {
        System.out.println("loading file spam");
        loadModelFile();
        utility.loadDictionary();
        utility.loadStopWords();
    }

    @WorkerThread
    public synchronized void loadModelFile(){
        try{
            buffer = loadModelFile(modelPath);
            interpreter = new Interpreter(buffer);
        }catch (IOException exp){
            exp.printStackTrace();
        }
    }

    /** Free up resources as the client is no longer needed. */
    @WorkerThread
    public synchronized void unload() {
        interpreter.close();
        utility.clear();
    }


    @WorkerThread
    public synchronized String classify(String text){

        float[][] input = utility.tokenizer(text);
        // output labels 0 & 1
        float[][] output = new float[1][1];

        // Run inference.
        Log.v("Classification", "Classifying text with TF Lite...");
        interpreter.run(input, output);
        Log.i("Classification", Float.toString(output[0][0]));

        // Return the probability of each class.
        return Float.toString(output[0][0]);
    }

    private MappedByteBuffer loadModelFile(String modelFilename)
            throws IOException {
        AssetFileDescriptor fileDescriptor = this.context.getAssets().openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}


