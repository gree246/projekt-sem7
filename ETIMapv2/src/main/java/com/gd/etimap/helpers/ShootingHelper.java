package com.gd.etimap.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.gd.etimap.objects.Enemy;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.qozix.tileview.TileView;

import java.util.List;

import solid.functions.Action1;
import solid.stream.Stream;

import static solid.collectors.ToList.toList;

/**
 * Created by Marcin on 29.10.2017.
 */

public class ShootingHelper {

    private static ImageView eighty;
    private static ImageView sixty;
    private static ImageView fourty;
    private static ImageView tweenty;
    private ImageTransformationHelper imageTransformationHelper = new ImageTransformationHelper();
    private DrawingHelper drawingHelper = new DrawingHelper();

    public ShootingHelper(int resEighty, int resSixty, int resFourty, int resTweenty, Context context) {
        eighty = imageTransformationHelper.createImageView(resEighty, context, false);
        sixty = imageTransformationHelper.createImageView(resSixty, context, false);
        fourty = imageTransformationHelper.createImageView(resFourty, context, false);
        tweenty = imageTransformationHelper.createImageView(resTweenty, context, false);
    }

    public void shoot(ListOfAllObjects listOfAllObjects, TileView tileView){
        Enemy shootedEnemy = (Enemy) isShooted(listOfAllObjects);
        if(shootedEnemy != null){
            removeAllDeadEnemies(listOfAllObjects, tileView);
            changePictureOfEnemy(shootedEnemy, tileView);
            shootedEnemy.setHp(shootedEnemy.getHp() - 20);
        }
    }

    private void removeAllDeadEnemies(ListOfAllObjects listOfAllObjects, TileView tileView){
        List<OurObject> listOfObjectsWithZeroHp = listOfAllObjects.findAllVisibleEnemiesWhichHpIsZero();
        Stream.stream(listOfObjectsWithZeroHp).forEach((Action1< OurObject>) e -> tileView.removeMarker(e.getMarker()));
        listOfAllObjects.getListOfOurObjects().removeAll(listOfObjectsWithZeroHp);
    }

    private OurObject isShooted(ListOfAllObjects listOfAllObjects){
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player").get(0);
        try{
            return Stream.stream(listOfAllObjects.findAllVisibleEnemies())
                    .map(e -> checkIfShooted(e, player))
                    .filter(e -> e != null).collect(toList()).get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private OurObject checkIfShooted(OurObject enemy, OurObject player){
        float angle = countAngle(enemy.getPoint().getX() - player.getPoint().getX(), enemy.getPoint().getY() - player.getPoint().getY());
        if(angle - enemy.getImageView().getRotation() < 5 || angle - enemy.getImageView().getRotation() > -5)
            return enemy;
        return null;
    }

    private void changePictureOfEnemy(OurObject ourObject, TileView tileView){
        Enemy enemy = changePicture(ourObject);
        tileView.removeMarker(enemy.getMarker());
        drawingHelper.draw(enemy, tileView);
    }

    private Enemy changePicture(OurObject ourObject){
        int hp = ((Enemy) ourObject).getHp();
        if(hp <= 80 && hp > 60){
            ourObject.setImageView(eighty);
        }else if(hp <= 60 && hp > 40){
            ourObject.setImageView(sixty);
        }else if(hp <= 40 && hp > 20){
            ourObject.setImageView(fourty);
        }else if(hp <= 20){
            ourObject.setImageView(tweenty);
        }
        return (Enemy) ourObject;
    }

    private float countAngle(double dx, double dy){
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if(angle < 0)
            angle += 360;
        return angle;
    }
}
