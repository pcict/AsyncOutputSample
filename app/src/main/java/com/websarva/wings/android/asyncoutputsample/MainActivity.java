package com.websarva.wings.android.asyncoutputsample;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    @Override
    @UiThread
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタンの取得
        Button btSync = findViewById(R.id.btSync);
        Button btAsync = findViewById(R.id.btAsync);
        Button btToast = findViewById(R.id.btToast);

        //ボタンにリスナーを登録
        btSync.setOnClickListener(new SyncClickListener());
        btAsync.setOnClickListener(new AsyncClickListener());
        btToast.setOnClickListener(new ToastClickListener());
    }

    //無駄ループメソッド
    public void LoopMethod(String threadName){
        try {
            Log.i("AsyncOutputSample", threadName + ":Loop開始");
            for(int i = 0; i < 10000; i++){
                System.out.println(threadName + ":" + i);
            }
            Log.i("AsyncOutputSample",  threadName + ":Loop終了");
        }catch (Exception ex){}
    }

    private class Receiver implements Callable<String> {

        @WorkerThread
        @Override
        public String call() {
            LoopMethod("WORKER");
            //UIスレッドに渡すデータ
            String result = "call()メソッド終了";
            return result;
        }
    }

    //同期ボタン
    private class SyncClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            LoopMethod("UI");
        }
    }

    //非同期ボタン
    private class AsyncClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            Receiver receiver = new Receiver();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit(receiver);

            String result ="";

            LoopMethod("UI");

            try{
                //ワーカースレッドからのリターンを待つ
                Log.i("AsyncOutputSample", "future.get()開始前");
                result = future.get();
                Log.i("AsyncOutputSample", "future.get()開始後");

            }
            catch (Exception ex){
                Log.w("DEBUG_TAG", "非同期処理の例外発生", ex);
            }

            TextView tvMsg = findViewById(R.id.tvMsg);
            tvMsg.setText(result);

        }
    }

    private class ToastClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i("AsyncOutputSample", "Toast.makeText()開始前");
            Toast.makeText(MainActivity.this,"トースト表示",Toast.LENGTH_LONG).show();
            Log.i("AsyncOutputSample", "Toast.makeText()開始後");
        }
    }
}