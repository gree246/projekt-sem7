package com.example.nataliasobolewska.androidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.nataliasobolewska.androidapp.Atributtes.Point;
import com.example.nataliasobolewska.androidapp.Atributtes.Position;
import com.example.nataliasobolewska.androidapp.Objects.ListOfAllObjects;
import com.example.nataliasobolewska.androidapp.Services.DrawObjectsService;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R2.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        doThreadsMalformation();
    }

    private void doThreadsMalformation(){
        ListOfAllObjects listOfAllObjects = new ListOfAllObjects();
        DrawObjectsService drawObjectsService = new DrawObjectsService(imageView);
        Random random = new Random();
        Timer t = new Timer();
        t.scheduleAtFixedRate(createTask(listOfAllObjects, drawObjectsService, random), 1000L, 2000L);
    }

    private TimerTask createTask(ListOfAllObjects listOfAllObjects, DrawObjectsService drawObjectsService, Random random){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Thread(() -> drawing(listOfAllObjects, drawObjectsService, random)));
            }
        };
        return task;
    }

    private void drawing(ListOfAllObjects listOfAllObjects, DrawObjectsService drawObjectsService, Random random){
        int randomNumber = createRandNumber(random);
        drawObjectsService.drawOnImageView(imageView, prepareListForDrawing(listOfAllObjects, randomNumber));
    }

    private ListOfAllObjects prepareListForDrawing(ListOfAllObjects listOfAllObjects, int randomNumber){
        listOfAllObjects.removeAllObjects();
        listOfAllObjects.createRectanglesData(new Position(new Point(randomNumber, randomNumber), new Point(randomNumber + 30, randomNumber + 30)));

        return listOfAllObjects;
    }

    private int createRandNumber(Random random){
        return random.nextInt(50) + 1;
    }
}
