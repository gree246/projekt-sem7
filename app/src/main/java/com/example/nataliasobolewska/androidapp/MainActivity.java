package com.example.nataliasobolewska.androidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.nataliasobolewska.androidapp.Atributtes.Point;
import com.example.nataliasobolewska.androidapp.Atributtes.Position;
import com.example.nataliasobolewska.androidapp.Objects.ListOfAllObjects;
import com.example.nataliasobolewska.androidapp.Objects.Rectangle;
import com.example.nataliasobolewska.androidapp.Services.DrawObjectsService;

import java.util.Random;

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
        draw();
    }

    private void draw(){
        ListOfAllObjects listOfAllObjects = new ListOfAllObjects(new Position(new Point(20, 20), new Point(50, 50)));
        DrawObjectsService drawObjectsService = new DrawObjectsService();
        Random r = new Random();
        int random = r.nextInt(30);


        listOfAllObjects.removeAllObjects();
        listOfAllObjects.createRectanglesData(new Position(new Point(random, random), new Point(random + 30,  random + 30)));

        for(int i=0; i< 2; i++){
            for(Rectangle rectangle: listOfAllObjects.getListOfRectangles()){
                drawObjectsService.drawOnImageView(imageView, rectangle);
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Stream.stream(listOfAllObjects.getListOfRectangles()).forEach((Rectangle r) ->  drawObjectsService.drawOnImageView(imageView, r));
    }

}
