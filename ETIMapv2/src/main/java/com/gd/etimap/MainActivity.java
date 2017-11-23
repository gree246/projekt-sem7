package com.gd.etimap;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import static com.gd.etimap.receivers.MyBroadcastReciver.wifiManager;

public class MainActivity extends AppCompatActivity {

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

    private static int updateGUIInterval  = 2000;
    private updateGUIThread updateGUIThread=new updateGUIThread();
    private Handler updateGUIHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ButterKnife.bind(this);
        createTileView();

        shootingHelper = new ShootingHelper(R.mipmap.enemy1, R.mipmap.enemy2, R.mipmap.enemy3, R.mipmap.enemy4, this);
        createObjectsHelper.createPlayer(listOfAllObjects, imageTransformationHelper.createImageView(R.mipmap.player2, this, false));
        ImageView bullet = imageTransformationHelper.createImageView(R.mipmap.bullet , this, false);
        bullet.setScaleX((float)0.3);
        bullet.setScaleY((float)0.3);
        createObjectsHelper.createBullet(listOfAllObjects, bullet);
        drawingHelper.drawPlayer(listOfAllObjects, tileView);

        doConnetion();
        doListenersAndTileLayout();
        updateGUIHandler.postDelayed(updateGUIThread, updateGUIInterval);
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

    private void doAnimation(){
        if(isAnimationOfBullet && AnimationOfBulletHelper.listOfShootedPoints.size() > 1){
            updateGUIInterval = 10;
            counter++;
            animationOfBulletHelper.doAnimationOfBullet(listOfAllObjects, counter, tileView);
            if(counter == (AnimationOfBulletHelper.listOfShootedPoints.size()-1)){
                counter = -1;
                isAnimationOfBullet = false;
                AnimationOfBulletHelper.listOfShootedPoints.clear();
                updateGUIInterval = 50;
            }
        }
        /*if(Math.random() < 0.5)
            siHelper.doEnemySi(listOfAllObjects, tileView);*/
        if(Math.random() < 0.02){
            drawingHelper.drawEnemy(listOfAllObjects, imageTransformationHelper.createImageView(R.mipmap.enemy0, this, false), tileView);
        }
    }

    class updateGUIThread implements Runnable {
        @Override
        public void run() {
            OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
            tileView.slideToAndCenterWithScale(player.getPoint().getX(),player.getPoint().getY(),1f);
            tileView.scrollToAndCenter(player.getPoint().getX(),player.getPoint().getY());
            doAnimation();
            wifiManager.startScan();
            updateGUIHandler.postDelayed(this, updateGUIInterval);
        }
    }
}
