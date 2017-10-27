package com.example.kamol.myapplication;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.content.Context;
import android.util.StringBuilderPrinter;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qozix.tileview.TileView;
import com.qozix.tileview.markers.MarkerLayout;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.x;
import static android.R.attr.y;

public class MainActivity extends AppCompatActivity {


    TextView wifiSignal;
    WifiManager wifiManager;
    private Timer timer;
    private TimerTask timerTask;
    private String nazwaPomiaru, wyslijIP;
    private EditText nazwaPomiaruET;
    private EditText wyslijIPET;
    private Button sendButton;
    private String resault="";
    MyBroadcastReciver reciver = new MyBroadcastReciver();;
    private TileView tileView;
    private MarkerLayout location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiSignal = (TextView)findViewById(R.id.wifiSignal);
        nazwaPomiaruET = (EditText) findViewById(R.id.numerPomiaruId) ;
        wyslijIPET = (EditText) findViewById(R.id.IPwyslijId) ;
        SharedPreferences settings = getSharedPreferences("nanana", 0);
        wyslijIP= settings.getString("wyslijIp","192.168.1.33");
        wyslijIPET.setText(wyslijIP);

        tileView = (TileView) findViewById(R.id.MapId);

        tileView.setSize( 8192, 8192 );  // the original size of the untiled imag
        tileView.defineBounds(0,100,0,100);
        tileView.addDetailLevel( 1f, "tiles/tile_%d_%d.png", 256, 256);
        location= new MarkerLayout(this);
//        tileView.setLongClickable(true);
//        tileView.
//
//        tileView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                tileView.addMarker(location,view.getX(),view.getY(),null,null);
//                return false;
//            }
//        });


        sendButton = (Button) findViewById(R.id.sendButtonId);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiManager.startScan();

            }
        });


        nazwaPomiaru= settings.getString("nazwaPomiaru","a001");
        nazwaPomiaruET.setText(nazwaPomiaru);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(reciver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //sendData(reciver.resault);

    }

//    protected void onResume() {
//        super.onResume();
//        try {
//            timer = new Timer();
//            timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    MyBroadcastReciver reciver = new MyBroadcastReciver();
//                    registerReceiver(reciver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//                    wifiManager.startScan();
//                }
//            };
//            timer.schedule(timerTask, 1000, 1000);
//        } catch (IllegalStateException e){
//            android.util.Log.i("Damn", "resume error");
//        }
//
//    }
//    public void onPause(){
//        super.onPause();
//        timer.cancel();
//    }


    class MyBroadcastReciver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuffer buffer = new StringBuffer();
            List<ScanResult> list = wifiManager.getScanResults();
            for(ScanResult scanResult : list)
            {
                buffer.append(scanResult.SSID);
                buffer.append("\n\n");
            }
            wifiSignal.setText(buffer);
            resault=buffer.toString();
            sendData(resault);



        }
        private void sendData(final String buffero) {
            wyslijIP = wyslijIPET.getText().toString();
            AsyncTask asyncTask = new AsyncTask() {


                @Override
                protected Object doInBackground(Object[] params) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date time = new Date();
                    String currentDateandTime = sdf.format(time);

                    String message = buffero;


                    Socket socket = null;
                    DataOutputStream os = null;
                    BufferedReader is = null;

                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(wyslijIP, 8888), 2000);
                        os = new DataOutputStream(socket.getOutputStream());
                        // is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    } catch (UnknownHostException e) {
                        // RPiStatus="Connection Error";
                    } catch (IOException e) {
                        //RPiStatus = "Connection Error";
                    }

                    if (socket == null || os == null ){//|| is == null) {
                        return null;
                    }

                    try{
                        os.writeChars(message);
                        //String responseLine = is.readLine();

//                    if(responseLine!=null) {
//                        String[] values = responseLine.split(";");
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");
//                        Date msgTime = new Date();
//                        try {
//                            msgTime = dateFormat.parse(values[0]);
//                        } catch (ParseException e) {
//                            //RPiStatus="Invalid date value";
//                            e.printStackTrace();
//                        }
//                        if (msgTime.after(lastRPiUpdate)) {
//                            lastRPiUpdate = msgTime;
//                            try {
//                                windAngle = Double.parseDouble(values[1]);
//                                windSpeed = Double.parseDouble(values[2]);
//                                RPiStatus = values[3];
//                            }catch (Exception e){
//                                RPiStatus="Invalid values";
//                            }
//
//                        }else{
//                            RPiStatus="Outdated values";
//                        }
//                    }
                        os.close();
                        // is.close();
                        socket.close();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    return null;
                }
            };

            asyncTask.execute();

        }
    }

}
