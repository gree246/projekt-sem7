package com.example.nataliasobolewska.androidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.nataliasobolewska.androidapp.Atributtes.Point;
import com.example.nataliasobolewska.androidapp.Objects.ListOfAllObjects;
import com.example.nataliasobolewska.androidapp.Services.DrawingService;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;
    ImageView imageAndro;

    @BindView(R.id.imageViewOfWarior)
    ImageView imageViewOfWarior;
    @BindView(R.id.imageViewOfWarior2)
    ImageView imageViewOfWarior2;

    private static long PERIOD = 200L;
    private static long DELAY = 100L;
    private static int ANDRO_X = 400;
    private static int ANDRO_Y = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prepareImageViewsToRound();
        doThreadsMalformation();
    }

    private void doThreadsMalformation(){
        ListOfAllObjects listOfAllObjects = new ListOfAllObjects();
        Random random = new Random();
        Timer t = new Timer();
        t.scheduleAtFixedRate(createTask(listOfAllObjects, random), DELAY, PERIOD);
    }

    private TimerTask createTask(ListOfAllObjects listOfAllObjects, Random random){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Thread(() -> drawing(listOfAllObjects, random)));
            }
        };
        return task;
    }

    private void drawing(ListOfAllObjects listOfAllObjects, Random random){
        if(imageView.getX()>imageView.getWidth())
        {
            imageView.setX(0);
        }
        else
        {
            imageView.setX(imageView.getX()+10);
        }

        DrawingService.drawEnemies(prepareListForDrawing(listOfAllObjects, random));
    }


    private ListOfAllObjects prepareListForDrawing(ListOfAllObjects listOfAllObjects, Random random){
        listOfAllObjects.removeAllObjects();
        listOfAllObjects.createObjects(new Point(createRandNumber(random) + ANDRO_X, createRandNumber(random) + ANDRO_Y), imageViewOfWarior, imageViewOfWarior2);

        return listOfAllObjects;
    }

    private int createRandNumber(Random random){
        return random.nextInt(100) + 1;
    }

    private void prepareImageViewsToRound(){
        imageAndro = (ImageView)findViewById(R.id.imageView2);
        imageView.setY(500);
        imageView.setScaleX(15);
        imageView.setScaleY(15);
        imageAndro.setY(ANDRO_Y);
        imageAndro.setX(ANDRO_X);
        imageViewOfWarior.setVisibility(View.INVISIBLE);
    }
}
