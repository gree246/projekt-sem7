package com.example.kamol.myapplication;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.content.Context;
import android.util.StringBuilderPrinter;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    TextView wifiSignal;
    WifiManager wifiManager;
    private Timer timer;
    private TimerTask timerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiSignal = (TextView)findViewById(R.id.wifiSignal);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        MyBroadcastReciver reciver = new MyBroadcastReciver();
        registerReceiver(reciver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    protected void onResume() {
        super.onResume();
        try {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    MyBroadcastReciver reciver = new MyBroadcastReciver();
                    registerReceiver(reciver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    wifiManager.startScan();
                }
            };
            timer.schedule(timerTask, 1000, 1000);
        } catch (IllegalStateException e){
            android.util.Log.i("Damn", "resume error");
        }

    }
    public void onPause(){
        super.onPause();
        timer.cancel();
    }

    class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuffer buffer = new StringBuffer();
            List<ScanResult> list = wifiManager.getScanResults();
            for(ScanResult scanResult : list)
            {
                buffer.append(scanResult);
                buffer.append("\n\n");
            }
            wifiSignal.setText(buffer);
        }
    }
}
