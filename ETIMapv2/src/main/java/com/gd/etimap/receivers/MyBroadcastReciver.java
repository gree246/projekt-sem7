package com.gd.etimap.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by Marcin on 20.11.2017.
 */

public class MyBroadcastReciver extends BroadcastReceiver {

    public static WifiManager wifiManager;
    private int x=0;
    private int y=0;
    private int floor=0;
    String serverIP="192.168.1.128";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            StringBuffer buffer = new StringBuffer();
            List<ScanResult> list = wifiManager.getScanResults();
            for (ScanResult scanResult : list) {
                buffer.append(scanResult.BSSID);
                buffer.append(" ");
                buffer.append(scanResult.level);
                buffer.append(" ");
                buffer.append(scanResult.frequency);
                buffer.append(";");
            }
            sendData(buffer.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendData(final String buffero) {

        AsyncTask asyncTask = new AsyncTask() {
            String responseLine="";

            @Override
            protected Object doInBackground(Object[] params) {
                String message = "a000;0;0;0|" + buffero;
                Socket socket = null;
                DataOutputStream os = null;
                BufferedReader is = null;

                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(serverIP, 8888), 2000);
                    os = new DataOutputStream(socket.getOutputStream());
                    is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (socket == null || os == null || is == null) {
                    return null;
                }
                try {
                    os.writeChars(message);
                    responseLine = is.readLine();
                    os.close();
                    is.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                x=Integer.parseInt(responseLine.split(";")[0]);
                y=Integer.parseInt(responseLine.split(";")[1]);
                floor=Integer.parseInt(responseLine.split(";")[2]);
            }
        };
        asyncTask.execute();
    }
}
