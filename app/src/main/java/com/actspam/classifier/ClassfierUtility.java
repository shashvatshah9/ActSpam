package com.actspam.classifier;

import android.content.Context;

import androidx.annotation.WorkerThread;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ClassfierUtility {

    private final static String DIC_PATH = "vocab.json";
    private final static String STOP_WORDS_PATH = "stopwords.txt";
    private static int MAX_LEN = 7865;
    private static HashMap<String, Integer> dic;
    private static HashSet<String> stopWordSet;
    private Context context;

    public ClassfierUtility(Context context){
        this.context = context;
    }

    public float[][] tokenizer(String message){
        float[] tmp = new float[MAX_LEN];
        List<String> array = Arrays.asList(message.split(" "));
        int index = 0;

        for (String word : array) {
            if (index >= MAX_LEN) {
                break;
            }
            if(!stopWordSet.contains(word))
                tmp[index++] = dic.containsKey(word) ? dic.get(word) : 0;
        }
        // Padding and wrapping.
        Arrays.fill(tmp, index, MAX_LEN - 1, 0);
        float[][] ans = {tmp};
        return ans;
    }

    @WorkerThread
    public synchronized void loadDictionary() {
        // load vocab.json file
        try (InputStream ins = this.context.getAssets().open(DIC_PATH)){
            int size = ins.available();
            byte[] buffer = new byte[size];
            ins.read(buffer);
            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> it = jsonObject.keys();
            dic = new HashMap<>();
            while(it.hasNext()){
                String word = it.next();
                dic.put(word, (Integer)jsonObject.get(word));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @WorkerThread
    public synchronized void loadStopWords(){
        try (InputStream ins = this.context.getAssets().open(STOP_WORDS_PATH)){
            int size = ins.available();
            byte[] buffer = new byte[size];
            ins.read(buffer);
            String removeWords = new String(buffer, "UTF-8");
            stopWordSet = new HashSet<>();
            for(String word : removeWords.split(",")){
                stopWordSet.add(word);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @WorkerThread
    public synchronized void clear() {
        dic.clear();
        stopWordSet.clear();
    }
}
