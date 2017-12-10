package com.gd.etimap.helpers;

import android.widget.ImageView;

import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.ListOfAllObjects;

/**
 * Created by Marcin on 28.10.2017.
 */

public class CreateObjectsHelper {

    public static final double PLAYER_X = 4545;
    public static final double PLAYER_Y = 4368;

    public void createPlayer(ListOfAllObjects listOfAllObjects, ImageView imageViewPlayer){
        listOfAllObjects.removeAllObjects();
        listOfAllObjects.createPlayer(new Point(PLAYER_X, PLAYER_Y), imageViewPlayer);
    }

    public void createBullets(ListOfAllObjects listOfAllObjects, ImageView imageViewBullet1, ImageView imageViewBullet2){
        imageViewBullet2.setScaleX((float)0.25);
        imageViewBullet2.setScaleY((float)0.25);
        listOfAllObjects.createBullet(imageViewBullet1, false);
        listOfAllObjects.createBullet(imageViewBullet2, true);
    }
}
