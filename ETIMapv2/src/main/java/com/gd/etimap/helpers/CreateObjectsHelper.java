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

    public void createPlayerAndArrowObjects(ListOfAllObjects listOfAllObjects, ImageView imageViewPlayer, ImageView imageViewArrow){
        listOfAllObjects.removeAllObjects();
        listOfAllObjects.createPlayer(new Point(PLAYER_X, PLAYER_Y), imageViewPlayer);
        listOfAllObjects.createArrow(imageViewArrow);
    }

    public void createBullet(ListOfAllObjects listOfAllObjects, ImageView imageViewBullet){
        listOfAllObjects.createBullet(imageViewBullet);
    }
}
