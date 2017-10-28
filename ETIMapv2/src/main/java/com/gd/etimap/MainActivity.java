package com.gd.etimap;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gd.etimap.helpers.CreateObjectsHelper;
import com.gd.etimap.helpers.DrawingHelper;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.qozix.tileview.TileView;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private ListOfAllObjects listOfAllObjects = new ListOfAllObjects();
    private TileView tileView = null;

    private DrawingHelper drawingHelper = new DrawingHelper();
    private CreateObjectsHelper createObjectsHelper = new CreateObjectsHelper();

    private static final int updateGUIInterval  = 50;
    private updateGUIThread updateGUIThread=new updateGUIThread();
    private Handler updateGUIHandler = new Handler();

    private int counter = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ButterKnife.bind(this);
        createTileView();

        createObjectsHelper.createPlayerObjects(listOfAllObjects, createImageView(R.mipmap.ic_launcher));
        drawingHelper.drawAllObjects(listOfAllObjects, tileView);

        doListenersAndTileLayout();
        updateGUIHandler.postDelayed(updateGUIThread, updateGUIInterval);
    }


    private ImageView createImageView(int resId){
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resId);
        return imageView;
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
        tileView.addDetailLevel( 1f, "tiles/tile_%d_%d.png", 256, 256);
    }

    private void doListenersAndTileLayout(){
        RelativeLayout tileLayout = (RelativeLayout) findViewById(R.id.MapLayoutId) ;
        tileLayout.addView(tileView);

        Button bLeft = (Button) findViewById(R.id.buttonLeft_id) ;
        bLeft.setOnClickListener(view -> drawingHelper.changePositionOfPlayer(listOfAllObjects, "x", "-"));

        Button bRight = (Button) findViewById(R.id.buttonRight_id) ;
        bRight.setOnClickListener(view -> drawingHelper.changePositionOfPlayer(listOfAllObjects, "x", "+"));

        Button bUp = (Button) findViewById(R.id.buttonUp_id) ;
        bUp.setOnClickListener(view -> drawingHelper.changePositionOfPlayer(listOfAllObjects, "y", "-"));

        Button bDown = (Button) findViewById(R.id.buttonDown_id) ;
        bDown.setOnClickListener(view -> drawingHelper.changePositionOfPlayer(listOfAllObjects, "y", "+"));
    }

    private void doAnimation(){
        counter++;
        if(counter < 300){
            drawingHelper.changePositionOfPlayer(listOfAllObjects, "x", "+");
        }else{
            drawingHelper.changePositionOfPlayer(listOfAllObjects, "x", "-");
            if(counter == 600){
                counter = 0;
            }
        }
        if(Math.random() < 0.02){
            drawingHelper.drawEnemy(listOfAllObjects, createImageView(R.mipmap.ic_launcher), tileView);
        }
    }

    private void doSomeCrazyStuffInEachIterationOfAnimation(){
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayer("Player").get(0);
        double x = player.getPoint().getX();
        double y = player.getPoint().getY();

        tileView.moveMarker(player.getImageView(),x,y);
        tileView.scrollToAndCenter(x,y);
        tileView.slideToAndCenterWithScale(x,y,1f);
    }

    class updateGUIThread implements Runnable {
        @Override
        public void run() {
            doAnimation();
            doSomeCrazyStuffInEachIterationOfAnimation();
            updateGUIHandler.postDelayed(this, updateGUIInterval);
        }
    }

}
