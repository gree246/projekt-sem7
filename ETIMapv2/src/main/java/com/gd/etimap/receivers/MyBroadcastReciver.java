package com.gd.etimap.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.gd.etimap.helpers.DrawingHelper;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.Player;
import com.qozix.tileview.TileView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import static com.gd.etimap.MainActivity.canSend;
import static com.gd.etimap.MainActivity.velX;
import static com.gd.etimap.MainActivity.velY;
import static com.gd.etimap.MainActivity.velZ;
import static com.gd.etimap.helpers.AnimationOfBulletHelper.isAnimationOfBullet;
import static com.gd.etimap.helpers.AnimationOfBulletHelper.isAnimationOfBullet2;

/**
 * Created by Marcin on 20.11.2017.
 */

public class MyBroadcastReciver extends BroadcastReceiver {

    public static WifiManager wifiManager;
    private double x = 4000;
    private double y = 4000;
    private int floor = 0;

    String serverIP = "192.168.137.1";
    Player player;
    TileView tileView;
    long sendingInterval = 500; //ms
    long lastUpdateTime = 0;

    DrawingHelper drawingHelper = new DrawingHelper();

    public MyBroadcastReciver(ListOfAllObjects listOfAllObjects, TileView tileView) {
        this.tileView = (TileView) tileView;
        player = (Player) listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        long now = System.currentTimeMillis();
        if (now - lastUpdateTime > sendingInterval) {
            lastUpdateTime = now;
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendData(final String buffero) {

        AsyncTask asyncTask = new AsyncTask() {
            String responseLine = "";

            @Override
            protected Object doInBackground(Object[] params) {
                String message = "a000;0;0;0;"+String.format("%.0f", player.getImageView().getRotation())+"|" + buffero;
                Socket socket = null;
                DataOutputStream os = null;
                BufferedReader is = null;

                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(serverIP, 8888), 1000);
                    os = new DataOutputStream(socket.getOutputStream());
                    is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (socket == null || os == null || is == null) {
                    return null;
                }
                try {
                    os.writeUTF(message);
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

                    try {
                        x = Double.parseDouble(responseLine.split(";")[0]);
                        y = Double.parseDouble(responseLine.split(";")[1]);
                        floor = Integer.parseInt(responseLine.split(";")[2]);
                    } catch (Exception e) {
                        x = player.getPoint().getX();
                        y = player.getPoint().getY();
                    }
                    double wsp = Math.sqrt(Math.pow((double)velX,2)+Math.pow((double)velX,2)+Math.pow((double)velX,2))/5;
                    if(wsp>1){
                        wsp=1;
                    }
                    wsp = 1;
                    player.getPoint().setX((1-wsp) * player.getPoint().getX() + wsp* x);
                    player.getPoint().setY((1-wsp) * player.getPoint().getY() + wsp * y);

                    // drawingHelper.changeFloor(tileView, floor);

                    tileView.moveMarker(player.getMarker(), player.getPoint().getX(), player.getPoint().getY());
                    tileView.slideToAndCenterWithScale(player.getPoint().getX(), player.getPoint().getY(), 1f);
                    tileView.scrollToAndCenter(player.getPoint().getX(), player.getPoint().getY());
                }

        };
        asyncTask.execute();
    }
}
