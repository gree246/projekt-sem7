package com.gd.etimap;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qozix.tileview.TileView;

public class MainActivity extends AppCompatActivity {

    private double x=4000;
    private double y=4000;
    private static final int updateGUIInterval  = 50;
    private updateGUIThread updateGUIThread=new updateGUIThread();
    private Handler updateGUIHandler = new Handler();

    TileView tileView=null;
    ImageView player =null;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main   );

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
//        tileView.defineBounds(0,100,0,100);

        player = new ImageView( this );
        player.setImageResource( R.mipmap.ic_launcher );
        tileView.addMarker(player,x,y,null,null);
        tileView.slideToAndCenter(x,y);

        RelativeLayout tileLayout = (RelativeLayout) findViewById(R.id.MapLayoutId) ;
                tileLayout.addView(tileView);


        Button bLeft = (Button) findViewById(R.id.buttonLeft_id) ;
        bLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {         x-=10;
            }
        });

        Button bRight = (Button) findViewById(R.id.buttonRight_id) ;
        bRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x+=10;
            }
        });

        Button bUp = (Button) findViewById(R.id.buttonUp_id) ;
        bUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                y-=10;
            }
        });

        Button bDown = (Button) findViewById(R.id.buttonDown_id) ;
        bDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                y+=10;
            }
        });


        updateGUIHandler.postDelayed(updateGUIThread, updateGUIInterval);




    }
    class updateGUIThread implements Runnable {


        @Override
        public void run() {

            tileView.moveMarker(player,x,y);
            tileView.scrollToAndCenter(x,y);
            tileView.slideToAndCenterWithScale(x,y,1f);
            updateGUIHandler.postDelayed(this, updateGUIInterval);


        }
    }

}
