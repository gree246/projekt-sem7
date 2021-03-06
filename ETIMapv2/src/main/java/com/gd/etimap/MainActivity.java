package com.gd.etimap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gd.etimap.helpers.AnimationOfBulletHelper;
import com.gd.etimap.helpers.CreateObjectsHelper;
import com.gd.etimap.helpers.DrawingHelper;
import com.gd.etimap.helpers.ImageTransformationHelper;
import com.gd.etimap.helpers.ShootingHelper;
import com.gd.etimap.helpers.SiHelper;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.gd.etimap.objects.Player;
import com.gd.etimap.receivers.MyBroadcastReciver;
import com.qozix.tileview.TileView;
import com.qozix.tileview.detail.DetailLevel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gd.etimap.helpers.AnimationOfBulletHelper.isAnimationOfBullet;
import static com.gd.etimap.helpers.AnimationOfBulletHelper.isAnimationOfBullet2;
import static com.gd.etimap.receivers.MyBroadcastReciver.wifiManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener{


    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    private Sensor linearAccSensor;
    boolean        havelinearAccSensor=false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    float[] acc = new float[3];

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp = 0;
    public static volatile float velX, velY, velZ, dT;


    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    @BindView(R2.id.rotateLeft_id)
    Button rotateLeftButton;
    @BindView(R2.id.rotateRight_id)
    Button rotateRightButton;
    @BindView(R2.id.buttonGun)
    Button gunButton;
    @BindView(R.id.buttonFloorDown)
    Button buttonFDown;
    @BindView(R.id.buttonFloorUp)
    Button buttonFUp;

    TextView accTextV;


    public static volatile double counter = -1;
    public static volatile double counter2 = -1;
    public static volatile boolean canSend = false;

    public static float scaleOfAvatars = (float)0.75;

    public static int floor = 0;

    private ListOfAllObjects listOfAllObjects = new ListOfAllObjects();
    private TileView tileView = null;
    private DetailLevel  detailLevel = null;

    private DrawingHelper drawingHelper = new DrawingHelper();
    private CreateObjectsHelper createObjectsHelper = new CreateObjectsHelper();
    private ImageTransformationHelper imageTransformationHelper = new ImageTransformationHelper();
    private ShootingHelper shootingHelper;
    private AnimationOfBulletHelper animationOfBulletHelper = new AnimationOfBulletHelper();
    private SiHelper siHelper = new SiHelper();
    private static Context toAnimationContext = null;

    private static int updateGUIInterval  = 100;
    private updateGUIThread updateGUIThread=new updateGUIThread();
    private Handler updateGUIHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ButterKnife.bind(this);
        createTileView();

        acc[0]=0;
        acc[1]=0;
        acc[2]=0;

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        start();

        shootingHelper = new ShootingHelper(R.mipmap.enemy1, R.mipmap.enemy2, R.mipmap.enemy3, R.mipmap.enemy4,
                R.mipmap.player1, R.mipmap.player2, R.mipmap.player3, R.mipmap.player4, this);
        ImageView player = imageTransformationHelper.createImageView(R.mipmap.player0, this, false);
        player.setScaleX(MainActivity.scaleOfAvatars);
        player.setScaleY(MainActivity.scaleOfAvatars);
        createObjectsHelper.createPlayer(listOfAllObjects, player);
        ImageView bullet = imageTransformationHelper.createImageView(R.mipmap.bullet , this, false);
        bullet.setScaleX((float)0.25);
        bullet.setScaleY((float)0.25);
        createObjectsHelper.createBullets(listOfAllObjects, bullet, imageTransformationHelper.createImageView(R.mipmap.bullet , this, false));
        drawingHelper.drawPlayer(listOfAllObjects, tileView);

        //SensorActivity sensorActivity = new SensorActivity(listOfAllObjects, tileView, this);
        doConnetion();
        doListenersAndTileLayout();
        updateGUIHandler.postDelayed(updateGUIThread, updateGUIInterval);

        toAnimationContext = this;
    }

    private void doConnetion(){
        MyBroadcastReciver reciver = new MyBroadcastReciver(listOfAllObjects,tileView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(reciver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void createTileView(){
        tileView = new TileView(this){
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                return false;
            }
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return false;
            }
            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                return false;
            }
        };
        tileView.setSize( 8192, 8192 );  // the original size of the untiled image
        detailLevel = new DetailLevel(tileView.getDetailLevelManager(),1f, "floor"+floor+"/tile_"+floor+"_%d_%d.png", 256, 256);
        tileView.onDetailLevelChanged(detailLevel);

    }

    private void doListenersAndTileLayout(){
        RelativeLayout tileLayout = (RelativeLayout) findViewById(R.id.MapLayoutId) ;
        tileLayout.setScaleX(1);
        tileLayout.setScaleY(1);
        tileLayout.setMinimumWidth(600);
        tileLayout.setMinimumHeight(600);

        tileLayout.addView(tileView);

        accTextV= (TextView)findViewById(R.id.textView001) ;

        Button bLeft = (Button) findViewById(R.id.buttonLeft_id) ;
        bLeft.setOnClickListener(view -> drawingHelper.changePositionOfPlayer(listOfAllObjects, "x", "-", tileView));

        Button bRight = (Button) findViewById(R.id.buttonRight_id) ;
        bRight.setOnClickListener(view -> drawingHelper.changePositionOfPlayer(listOfAllObjects, "x", "+", tileView));

        Button bUp = (Button) findViewById(R.id.buttonUp_id) ;
        bUp.setOnClickListener(view -> drawingHelper.changePositionOfPlayer(listOfAllObjects, "y", "-", tileView));

        Button bDown = (Button) findViewById(R.id.buttonDown_id) ;
        bDown.setOnClickListener(view -> drawingHelper.changePositionOfPlayer(listOfAllObjects, "y", "+", tileView));

        buttonFDown.setOnClickListener(view -> DrawingHelper.changeFloorDown(tileView));
        buttonFUp.setOnClickListener(view -> DrawingHelper.changeFloorUp(tileView));

        rotateLeftButton.setOnClickListener(view -> imageTransformationHelper.rotate(listOfAllObjects, false, tileView));
        rotateRightButton.setOnClickListener(view -> imageTransformationHelper.rotate(listOfAllObjects, true, tileView));
        gunButton.setOnClickListener(view -> shootingHelper.shoot(listOfAllObjects, tileView));
    }

    private void bulletPlayerAnimation(){
        if(isAnimationOfBullet && AnimationOfBulletHelper.listOfShootedPoints.size() > 1){
            counter++;
            animationOfBulletHelper.doAnimationOfBullet(listOfAllObjects, counter, tileView);
            if(counter == (AnimationOfBulletHelper.listOfShootedPoints.size()-1)){
                counter = -1;
                isAnimationOfBullet = false;
                AnimationOfBulletHelper.listOfShootedPoints.clear();
            }
        }
    }

    private void bulletEnemyAnimation(){
        if(isAnimationOfBullet2 && AnimationOfBulletHelper.listOfShootedPoints2.size() > 1){
            counter2++;
            animationOfBulletHelper.doAnimationOfBulletForEnemy(listOfAllObjects, counter2, tileView);
            if(counter2 == (AnimationOfBulletHelper.listOfShootedPoints2.size()-1)){
                counter2 = -1;
                isAnimationOfBullet2 = false;
                AnimationOfBulletHelper.listOfShootedPoints2.clear();
            }
        }
    }

    private void doAnimation(){
        bulletPlayerAnimation();
        bulletEnemyAnimation();


//        if(Math.random() > 0.5)
//            shootingHelper.shootToPlayer(listOfAllObjects, tileView);
//        if(Math.random() < 0.5)
            siHelper.doEnemySi(listOfAllObjects, tileView, shootingHelper);
        if(Math.random() < 0.25){
            drawingHelper.drawEnemy(listOfAllObjects, imageTransformationHelper.createImageView(R.mipmap.enemy0, this, false), tileView);
        }
    }

    class updateGUIThread implements Runnable {
        @Override
        public void run() {
            if(ShootingHelper.theEnd){
                if(!findViewById(android.R.id.content).equals(R.layout.koniec)){
                    setContentView( R.layout.koniec );
                    TextView text = (TextView)findViewById(R.id.endView);
//                    RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(toAnimationContext, R.anim.rotate_animation);
//                    text.setAnimation(rotate);
                    Button bEnd = (Button) findViewById(R.id.buttonEnd);
                    bEnd.setOnClickListener(view -> reset());
                }
            }else{
                OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
                tileView.slideToAndCenterWithScale(player.getPoint().getX(),player.getPoint().getY(),1f);
                tileView.scrollToAndCenter(player.getPoint().getX(),player.getPoint().getY());
                doAnimation();
                wifiManager.startScan();

                updateGUIHandler.postDelayed(this, updateGUIInterval);
            }
        }
    }

    public void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
        linearAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        havelinearAccSensor = mSensorManager.registerListener(this, linearAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop() {
        if (haveSensor) {
            mSensorManager.unregisterListener(this, mRotationV);
        }
        else {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        }
        if(havelinearAccSensor){
            mSensorManager.unregisterListener(this,linearAccSensor);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 180) % 360;
        }

        if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){


            if (timestamp == 0) {
                timestamp = event.timestamp;
            }
            dT = (event.timestamp - timestamp) * NS2S;
            timestamp = event.timestamp;
            velX += event.values[0] * dT;
            velY += event.values[1] * dT;
            velZ += event.values[2] * dT;
            if(event.values[0]<0.1 && event.values[0]>-0.1) {
                velX=0;
            }
            if(event.values[1]<0.1 && event.values[1]>-0.1) {
                velY=0;
            }
            if(event.values[2]<0.1 && event.values[2]>-0.1) {
                velZ=0;
            }
            accTextV.setText( String.format("%.3f", velX)+"  "+ String.format("%.3f",velY)+"  "+ String.format("%.3f", velZ));


//            acc[0]=0.9f*acc[0]+0.1f*event.values[0];
//            acc[1]=0.9f*acc[1]+0.1f*event.values[1];
//            acc[2]=0.9f*acc[2]+0.1f*event.values[2];
//            accTextV.setText( String.format("%.3f", acc[0])+"  "+ String.format("%.3f", acc[1])+"  "+ String.format("%.3f", acc[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 180) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
//        accTextV.setText( String.format("%.3f", acc[0])+"  "+ String.format("%.3f", acc[1])+"  "+ String.format("%.3f", acc[2]));


        //DO ODKOMENTOWANIA
        imageTransformationHelper.rotateFromSensor(listOfAllObjects, tileView, -mAzimuth);

        String where = "NW";

        if (mAzimuth >= 350 || mAzimuth <= 10)
            where = "N";
        if (mAzimuth < 350 && mAzimuth > 280)
            where = "NW";
        if (mAzimuth <= 280 && mAzimuth > 260)
            where = "W";
        if (mAzimuth <= 260 && mAzimuth > 190)
            where = "SW";
        if (mAzimuth <= 190 && mAzimuth > 170)
            where = "S";
        if (mAzimuth <= 170 && mAzimuth > 100)
            where = "SE";
        if (mAzimuth <= 100 && mAzimuth > 80)
            where = "E";
        if (mAzimuth <= 80 && mAzimuth > 10)
            where = "NE";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void reset()
    {
        ShootingHelper.theEnd = false;
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
        ((Player) player).setHp(500);
        setContentView( R.layout.activity_main );
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }
}

