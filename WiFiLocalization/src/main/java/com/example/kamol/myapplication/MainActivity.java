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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qozix.tileview.TileView;

import org.w3c.dom.Text;

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
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


//    TextView wifiSignal;
    public boolean error = false;
    WifiManager wifiManager;
    WifiManager.WifiLock wifiLock;
    private Timer timer;
    private TimerTask timerTask;
    private String nazwaPomiaru, wyslijIP;
    private EditText nazwaPomiaruET;
    private EditText wyslijIPET;
    private TextView status;
    private Button sendButton;
    private Button centerButton;
    private String resault="";
    private TileView tileView = null;
    private int centerX=0;
    private int centerY=0;
    private int floor=1;
    private boolean send = false;
    private String previousData;
    MyBroadcastReciver reciver = new MyBroadcastReciver();;

    class getAsyncTask extends AsyncTask<Void, Void,Void>{
        String out;
        //GET network request
        private String GET(OkHttpClient client, HttpUrl url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.SECONDS)
                    .writeTimeout(1, TimeUnit.SECONDS)
                    .readTimeout(1, TimeUnit.SECONDS)
                    .build();

            error=false;
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http") //http
                    .host("etiinawazja.000webhostapp.com")
                    .port(80)
                    .addPathSegment("getPreviousData.php")//adds "/pathSegment" at the end of hostname
                    .build();
            try {
                out=GET(client, url);


            } catch (IOException e) {
//                    e.printStackTrace();
                Log.d("sending", "error");
                error = true;
            } catch (Exception e) {
                Log.e("OTHER EXCEPTIONS", e.toString());
                error = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (error) {
                status.setText("ERROR");
                previousData = "";
            }
            else {
                status.setText("ok");
                previousData = out;
            }

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences("nanana", 0);

        nazwaPomiaruET = (EditText) findViewById(R.id.numerPomiaruId) ;
        nazwaPomiaru= settings.getString("nawaPomiaru","a001");
        nazwaPomiaruET.setText(nazwaPomiaru);

        status= (TextView)findViewById(R.id.statusId);

        wyslijIPET = (EditText) findViewById(R.id.IPwyslijId) ;
        wyslijIP= settings.getString("wyslijIp","95.160.153.43");
        wyslijIPET.setText(wyslijIP);

        tileView = new TileView(this);
        tileView.setSize( 8192, 8192 );  // the original size of the untiled image
        tileView.addDetailLevel( 1f, "floor1/tile_1_%d_%d.png", 256, 256);
//        tileView.slideToAndCenterWithScale(4000,4000,1f);
        tileView.setScale(1.0f);
        tileView.scrollToAndCenter(4000,4000);
        tileView.defineBounds(0,8192,0,8192);

        final RelativeLayout tileLayout = (RelativeLayout) findViewById(R.id.MapLayoutId) ;
        tileLayout.addView(tileView);
        ImageView pozycja = new ImageView(this);
        pozycja.setImageResource(R.drawable.crosshair2);
        pozycja.setMaxWidth(30);
        pozycja.setMaxHeight(30);
        tileLayout.addView(pozycja);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) pozycja.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT,
                RelativeLayout.TRUE);
        pozycja.setLayoutParams(params);


        tileView.scrollTo(4500,4500);


        centerButton = (Button) findViewById(R.id.centerButtonId);
        centerButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {

                                              nazwaPomiaru=nazwaPomiaruET.getText().toString();
                                              String[] lines = previousData.split(System.getProperty("line.separator"));
                                              int index=Integer.parseInt(nazwaPomiaru.substring(1));
                                              for (String line:lines
                                                   ) {
                                                  try {
                                                      int idx2=Integer.parseInt(line.substring(1, 4));
                                                      if (idx2== index) {

                                                          double x = Double.parseDouble(line.split("\\|")[0].split(";")[2]);
                                                          double y = Double.parseDouble(line.split("\\|")[0].split(";")[3]);
                                                          double scale=tileView.getScale();
                                                          tileView.slideToAndCenter((int) Math.round(scale*x),(int) Math.round(scale*y));
                                                          x=tileView.getScrollX();
                                                          y=tileView.getScrollY();
                                                          break;
                                                      }
                                                  }
                                                  catch(Exception e){
                                                          status.setText("errorStringParse");
                                                      }


                                              }

                                          }
                                      });

        sendButton = (Button) findViewById(R.id.sendButtonId);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                wyslijIP=wyslijIPET.getText().toString();
              //  centerX=tileView.getCoordinateTranslater().translateAndScaleX(tileView.getScrollX(),tileView.getScaleX())+256;
               // centerY=tileView.getCoordinateTranslater().translateAndScaleY(tileView.getScrollY(),tileView.getScaleY())+256;
                centerX = Math.round(tileView.getScrollX()/tileView.getScale()) + Math.round(tileView.getWidth()/2/tileView.getScale());
                centerY = Math.round(tileView.getScrollY()/tileView.getScale()) + Math.round(tileView.getHeight()/2/tileView.getScale());

//                centerY=tileView.getScrollY();
                nazwaPomiaru=nazwaPomiaruET.getText().toString();
                nazwaPomiaruET.setText(nazwaPomiaru.substring(0,1)+String.format("%03d", Integer.parseInt(nazwaPomiaru.substring(1))+1));
//                SharedPreferences settings = getSharedPreferences("nanana", 0);
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString("nazwaPomiaru", nazwaPomiaruET.getText().toString());
//                editor.putString("wyslijIp", wyslijIP);
//                editor.commit();


//                nazwaPomiaruET.setText(Integer.toString(tileView.getScrollX()));
                send=true;
                if(wifiManager.startScan()){
                    status.setText("wait");
                };
                centerButton.callOnClick();




            }
        });




        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(reciver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.setWifiEnabled(true);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "sdgs");
        wifiLock.setReferenceCounted(true);
        wifiLock.acquire();

        getAsyncTask atask = new getAsyncTask();
        atask.execute();

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


    class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                StringBuffer buffer = new StringBuffer();
                List<ScanResult> list = wifiManager.getScanResults();
                for (ScanResult scanResult : list) {
                    status.setText(Integer.toString(buffer.length()));
                    buffer.append(scanResult.BSSID);
                    buffer.append(" ");
                    buffer.append(scanResult.level);
                    buffer.append(" ");
                    buffer.append(scanResult.frequency);
                    buffer.append(" ");
                    buffer.append(scanResult.SSID);
                    buffer.append(";");
                }
//            wifiSignal.setText(buffer);
                resault = buffer.toString();
                status.setText("getWifi1");
                if (send) {
                    status.setText("getWifi2");
                    sendDataHttp(resault);

                    send = false;
                }
            }catch (Exception e){
                status.setText(e.toString());
            }

        }

        private void sendData(final String buffero) {
            wyslijIP = wyslijIPET.getText().toString();
            AsyncTask asyncTask = new AsyncTask() {

                String responseLine="";


                @Override
                protected Object doInBackground(Object[] params) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date time = new Date();
                    String currentDateandTime = sdf.format(time);

                    String message = nazwaPomiaru + ";" + Integer.toString(floor) + ";" + Integer.toString(centerX) + ";" + Integer.toString(centerY) + "|" + buffero;


                    Socket socket = null;
                    DataOutputStream os = null;
                    BufferedReader is = null;

                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(wyslijIP, 8888), 2000);
                        os = new DataOutputStream(socket.getOutputStream());
                        is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    } catch (UnknownHostException e) {
                        // RPiStatus="Connection Error";
                    } catch (IOException e) {
                        //RPiStatus = "Connection Error";
                    }

                    if (socket == null || os == null || is == null) {
                        return null;
                    }

                    try {
                        os.writeChars(message);
                        responseLine = is.readLine();

//                    if(responseLine!=null) {
//
//                    }
                        os.close();
                        is.close();
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

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    status.setText(responseLine);
                }


            };

            asyncTask.execute();

        }



        //POST network request
        public String POST(OkHttpClient client, HttpUrl url, RequestBody body) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        class mAsyncTask extends AsyncTask<String, Void,Void>{
            @Override
            protected Void doInBackground(String... params) {
                String buf=params[0];
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.SECONDS)
                        .writeTimeout(1, TimeUnit.SECONDS)
                        .readTimeout(1, TimeUnit.SECONDS)
                        .build();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date time = new Date();
                String currentDateandTime = sdf.format(time);
                Integer currentTimeMiliseconds = Integer.parseInt(currentDateandTime.split("\\.")[1]);

                error = false;
                String message = nazwaPomiaru + ";" + Integer.toString(floor) + ";" + Integer.toString(centerX) + ";" + Integer.toString(centerY) + "|" + buf+"\n";


                RequestBody body = new FormBody.Builder()
                        .add("action", "login")
                        .add("format", "json")
                        .add("message", message)
                        .build();


                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http") //http
                        .host("etiinawazja.000webhostapp.com")
                        .port(80)
                        .addPathSegment("saveWiFi2File.php")//adds "/pathSegment" at the end of hostname
                        .build();
                try {
                    Log.d("sending", POST(client, url, body));


                } catch (IOException e) {
//                    e.printStackTrace();
                    Log.d("sending", "error");
                    error = true;
                } catch (Exception e) {
                    Log.e("OTHER EXCEPTIONS", e.toString());
                    error = true;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (error)
                    status.setText("ERROR");
                else
                    status.setText("ok");
            }


        }




        private void sendDataHttp(String buffero) {
            mAsyncTask asyncTask = new mAsyncTask();
            asyncTask.execute(buffero);
        }
    }

}
