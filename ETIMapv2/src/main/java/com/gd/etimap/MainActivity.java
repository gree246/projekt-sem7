package com.gd.etimap;

import android.content.Context;
import android.content.DialogInterface;
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
    float[] rMat = new float[9];
    float[] orientation = new float[3];
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

    private static int updateGUIInterval  = 70;
    private updateGUIThread updateGUIThread=new updateGUIThread();
    private Handler updateGUIHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ButterKnife.bind(this);
        createTileView();

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
        registerReceiver(reciver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
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

        if(Math.random() > 0.5)
            shootingHelper.shootToPlayer(listOfAllObjects, tileView);
        if(Math.random() < 0.5)
            siHelper.doEnemySi(listOfAllObjects, tileView);
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
                    RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(toAnimationContext, R.anim.rotate_animation);
                    text.setAnimation(rotate);
                }
            }else{
                OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
                tileView.slideToAndCenterWithScale(player.getPoint().getX(),player.getPoint().getY(),1f);
                tileView.scrollToAndCenter(player.getPoint().getX(),player.getPoint().getY());
                doAnimation();
                canSend = true;
                wifiManager.startScan();
                canSend = false;
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
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
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
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);

        //DO ODKOMENTOWANIA
        //imageTransformationHelper.rotateFromSensor(listOfAllObjects, tileView, -mAzimuth);

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
}

