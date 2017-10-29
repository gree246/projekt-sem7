package com.gd.etimap.helpers;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
        rotateObject(listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player").get(0), tileView, rightDirection);
        OurObject arrow = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Arrow").get(0);
        rotateObject(arrow, tileView, rightDirection);
        arrow.setPoint(new Point(arrow.getPoint().getX(), arrow.getPoint().getY()));
    }

    private void rotateObject(OurObject ourObject, TileView tileView, boolean rightDirection){
        tileView.removeMarker(ourObject.getMarker());
        float angle = rightDirection ? 10f : -10f;
        tileView.addMarker(rotateImageByAngle(ourObject.getImageView(), angle), ourObject.getPoint().getX(), ourObject.getPoint().getY(), null, null);
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
