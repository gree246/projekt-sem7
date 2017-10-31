package com.gd.etimap.helpers;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.qozix.tileview.TileView;

import solid.functions.Action1;
import solid.stream.Stream;

/**
 * Created by Marcin on 28.10.2017.
 */

public class ImageTransformationHelper {

    public static final int ARROW_IMAGE_WIDTH = 100;
    public static final int ARROW_IMAGE_HEIGHT = 150;

    public void rotate(ListOfAllObjects listOfAllObjects, boolean rightDirection, TileView tileView){
        if(rightDirection) {
            tileView.setRotation(tileView.getRotation()-10);
        } else {
            tileView.setRotation(tileView.getRotation()+10);
        }

        // rotacja strzałki
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player").get(0);
        rotateObject(player, rightDirection);
        OurObject arrow = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Arrow").get(0);
        rotateObject(arrow, rightDirection);
    }

    private void rotateAllVisibleEnemies(ListOfAllObjects listOfAllObjects){
        Stream.stream(listOfAllObjects.findAllVisibleEnemies()).
                forEach((Action1< OurObject>) o -> rotateImageByAngle(o.getImageView(), listOfAllObjects.
                        findAllEnemiesOrPlayerOrArrow("Player").get(0).getImageView().getRotation()));
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
        if(isArrow) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ARROW_IMAGE_WIDTH, ARROW_IMAGE_HEIGHT);
            imageView.setLayoutParams(layoutParams);
        }
        return imageView;
    }
}
