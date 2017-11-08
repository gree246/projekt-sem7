package com.gd.etimap;

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
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.qozix.tileview.TileView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gd.etimap.helpers.AnimationOfBulletHelper.isAnimationOfBullet;

public class MainActivity extends AppCompatActivity {

    @BindView(R2.id.rotateLeft_id)
    Button rotateLeftButton;
    @BindView(R2.id.rotateRight_id)
    Button rotateRightButton;
    @BindView(R2.id.buttonGun)
    Button gunButton;

    public static volatile double counter = -1;

    private ListOfAllObjects listOfAllObjects = new ListOfAllObjects();
    private TileView tileView = null;

    private DrawingHelper drawingHelper = new DrawingHelper();
    private CreateObjectsHelper createObjectsHelper = new CreateObjectsHelper();
    private ImageTransformationHelper imageTransformationHelper = new ImageTransformationHelper();
    private ShootingHelper shootingHelper;
    AnimationOfBulletHelper animationOfBulletHelper = new AnimationOfBulletHelper();

    private static int updateGUIInterval  = 50;
    private updateGUIThread updateGUIThread=new updateGUIThread();
    private Handler updateGUIHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ButterKnife.bind(this);
        createTileView();

        shootingHelper = new ShootingHelper(R.mipmap.enemy1, R.mipmap.enemy2, R.mipmap.enemy3, R.mipmap.enemy4, this);
        createObjectsHelper.createPlayerAndArrowObjects(listOfAllObjects, imageTransformationHelper.createImageView(R.mipmap.player, this, false), imageTransformationHelper.createImageView(R.mipmap.arrow, this, true));
        ImageView bullet = imageTransformationHelper.createImageView(R.mipmap.bullet2 , this, false);
        bullet.setScaleX((float)0.3);
        bullet.setScaleY((float)0.3);
        createObjectsHelper.createBullet(listOfAllObjects, bullet);
        drawingHelper.drawPlayerAndArrowObjects(listOfAllObjects, tileView);

        doListenersAndTileLayout();
        updateGUIHandler.postDelayed(updateGUIThread, updateGUIInterval);
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
        tileView.addDetailLevel( 1f, "floor1/tile_1_%d_%d.png", 256, 256);

    }

    private void doListenersAndTileLayout(){
        RelativeLayout tileLayout = (RelativeLayout) findViewById(R.id.MapLayoutId) ;
        tileLayout.setScaleX(2);
        tileLayout.setScaleY(2);
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

        if(Math.random() < 0.01){
            drawingHelper.drawEnemy(listOfAllObjects, imageTransformationHelper.createImageView(R.mipmap.enemy0, this, false), tileView);
        }
    }

    class updateGUIThread implements Runnable {
        @Override
        public void run() {
            OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrArrowOrBullet("Player").get(0);
            tileView.slideToAndCenterWithScale(player.getPoint().getX(),player.getPoint().getY(),1f);
            tileView.scrollToAndCenter(player.getPoint().getX(),player.getPoint().getY());
            doAnimation();
            updateGUIHandler.postDelayed(this, updateGUIInterval);
        }
    }
}
