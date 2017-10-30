package com.gd.etimap.helpers;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import 	java.lang.Math;

import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.qozix.tileview.TileView;

/**
 * Created by Marcin on 28.10.2017.
 */

public class ImageTransformationHelper {

    public static final int ARROW_IMAGE_WIDTH = 100;
    public static final int ARROW_IMAGE_HEIGHT = 150;

    public void rotate(ListOfAllObjects listOfAllObjects, boolean rightDirection, TileView tileView){
        if(rightDirection)
        {
            tileView.setRotation(tileView.getRotation()-10);
        }
        else
        {
            tileView.setRotation(tileView.getRotation()+10);
        }

        // rotacja strzałki
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player").get(0);
        rotateObject(player, tileView, rightDirection);
        OurObject arrow = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Arrow").get(0);
        rotateArrow(player, arrow, tileView, rightDirection);


//        rotateObject(listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player").get(0), tileView, rightDirection);
//        OurObject arrow = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Arrow").get(0);
//        rotateObject(arrow, tileView, rightDirection);
//        arrow.setPoint(new Point(arrow.getPoint().getX(), arrow.getPoint().getY()));
    }

    private void rotateArrow(OurObject player, OurObject arrow, TileView tileView, boolean rightDirection)
    {
        float angle = rightDirection ? 10f : -10f;
        float x, y, z, alpha;
//        tileView.removeMarker(arrow.getMarker());
        z = (float) (Math.abs((float)(arrow.getPoint().getY() - player.getPoint().getY())) * Math.sqrt((float)(2*(1 - Math.cos(angle)))));
        alpha = (180 - angle) / 2;
        x = z * alpha;
        y = (float) Math.sqrt((float)(z * z - x * x));
        // tutaj według moich wyliczeń powinno się zgadzać (ewentualnie można zamienić plus z minusem)
        // ale się coś psuje, bo po dodaniu tych dwóch linijek wszystko znika ;/
//        arrow.getPoint().setX(arrow.getPoint().getX() + y);
//        arrow.getPoint().setY(arrow.getPoint().getY() - x);
//        tileView.addMarker(rotateImageByAngle(arrow.getImageView(), angle), arrow.getPoint().getX(), arrow.getPoint().getY(), null,null);
       arrow.setImageView(rotateImageByAngle(arrow.getImageView(), angle));
//               , arrow.getPoint().getX(), arrow.getPoint().getY(), -0.5f, -0.5f);
    }

    private void rotateObject(OurObject ourObject, TileView tileView, boolean rightDirection){

//        tileView.removeMarker(ourObject.getMarker());
//        float angle = rightDirection ? 10f : -10f;
//        tileView.addMarker(rotateImageByAngle(ourObject.getImageView(), angle), ourObject.getPoint().getX(), ourObject.getPoint().getY(), -0.5f, -0.5f);

//        tileView.removeMarker(ourObject.getMarker());
        float angle = rightDirection ? 10f : -10f;
        ourObject.setImageView(rotateImageByAngle(ourObject.getImageView(), angle));
    }

    private ImageView rotateImageByAngle(ImageView imageView, float angle){

        imageView.setRotation(imageView.getRotation() + angle);

        return imageView;
    }

    public ImageView createImageView(int resId, Context context, boolean isArrow){
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(resId);
        if(isArrow) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ARROW_IMAGE_WIDTH, ARROW_IMAGE_HEIGHT);
            imageView.setLayoutParams(layoutParams);
        }
        return imageView;
    }
}
