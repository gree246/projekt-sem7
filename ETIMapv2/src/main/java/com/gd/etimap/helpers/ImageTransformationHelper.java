package com.gd.etimap.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.qozix.tileview.TileView;

/**
 * Created by Marcin on 28.10.2017.
 */

public class ImageTransformationHelper {

    public void rotateFromSensor(ListOfAllObjects listOfAllObjects, TileView tileView, int azimuth){
        tileView.setRotation(azimuth);
        listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0).getMarker().setRotation(azimuth);
    }

    public void rotate(ListOfAllObjects listOfAllObjects, boolean rightDirection, TileView tileView){
        if(rightDirection) {
            tileView.setRotation(tileView.getRotation()-10);
        } else {
            tileView.setRotation(tileView.getRotation()+10);
        }

        // rotacja strzałki
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
        rotateObject(player, rightDirection);
    }

    private void rotateObject(OurObject ourObject, boolean rightDirection){
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

        return imageView;
    }
}
