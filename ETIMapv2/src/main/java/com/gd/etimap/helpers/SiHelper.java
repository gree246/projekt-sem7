package com.gd.etimap.helpers;

import com.gd.etimap.MainActivity;
import com.gd.etimap.atributtes.EnemyAnimation;
import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.Enemy;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.qozix.tileview.TileView;

import java.util.List;

import solid.functions.Action1;
import solid.stream.Stream;

import static solid.collectors.ToList.toList;

/**
 * Created by Marcin on 09.11.2017.
 */

public class SiHelper {

    DrawingHelper drawingHelper = new DrawingHelper();


    public void doEnemySi(ListOfAllObjects listOfAllObjects, TileView tileView){
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
        List<OurObject> listOfVisibleEnemies = listOfAllObjects.findAllVisibleEnemies();
        List<OurObject> listOfNewVisibleEnemies = getNewEnemies(listOfVisibleEnemies);
        List<OurObject> listOfOldVisibleEnemies = getOldEnemies(listOfVisibleEnemies);

//        Stream.stream(listOfNewVisibleEnemies).forEach((Action1<OurObject>) o -> changePositionOfNewEnemies(o, player));
//        Stream.stream(listOfOldVisibleEnemies).forEach((Action1<OurObject>) o -> changePositionOfOldEnemies(o, tileView));


        Stream.stream(listOfVisibleEnemies).forEach((Action1<OurObject>) o -> changePositionOfOldEnemies(o, tileView));
    }

    private List<OurObject> getNewEnemies(List<OurObject> listOfVisibleEnemies){
        return Stream.stream(listOfVisibleEnemies).filter(e -> !((Enemy) e).isSi()).collect(toList());
    }

    private List<OurObject> getOldEnemies(List<OurObject> listOfVisibleEnemies){
        return Stream.stream(listOfVisibleEnemies).filter(e -> ((Enemy) e).isSi()).collect(toList());
    }

    private void changePositionOfNewEnemies(OurObject ourObject, OurObject player){
//        if (player.getPoint().getX() > ourObject.getPoint().getX()){
//            ((Enemy) ourObject).setEnemyAnimation(new EnemyAnimation(true, 0));
//        }else{
//            ((Enemy) ourObject).setEnemyAnimation(new EnemyAnimation(false, 0));
//        }
//        ((Enemy) ourObject).setSi(true);
    }

    private void changePositionOfOldEnemies(OurObject ourObject, TileView tileView){
        Enemy enemy = ((Enemy) ourObject);
//        if(enemy.getEnemyAnimation().isDown())
//            ourObject.setPoint(new Point(ourObject.getPoint().getX() + 10, ourObject.getPoint().getY()));
//        else
//            ourObject.setPoint(new Point(ourObject.getPoint().getX() - 10, ourObject.getPoint().getY()));
//
//        if(enemy.getEnemyAnimation().getTime() >= 20){
//            enemy.getEnemyAnimation().setTime(0);
//            enemy.getEnemyAnimation().setDown(!enemy.getEnemyAnimation().isDown());
//        }
//        enemy.getEnemyAnimation().setTime(enemy.getEnemyAnimation().getTime() + 1);
        double rand = Math.random();
        char signx, signy;
        double pointX, pointY;
        pointX = 0;
        pointY = 0;

        signx = '+';
        signy = '+';

        if(MainActivity.floor == -1)
        {
            if(enemy.getPoint().getX() <= 1276)
                signx = '+';
            else if(enemy.getPoint().getX() >= 6858)
                signx = '-';
            else if(enemy.getPoint().getX() > 5267)
            {
                if(rand < 0.7)
                    signx = '-';
                else
                    signx = '+';
            }
            else if(enemy.getPoint().getX() < 2867)
            {
                if(rand < 0.7)
                    signx = '+';
                else
                    signx = '-';
            }
            else
            {
                if(rand < 0.5)
                    signx = '+';
                else
                    signx = '-';
            }

            if(enemy.getPoint().getY() <= 4238)
                signy = '+';
            else if(enemy.getPoint().getY() >= 4268)
                signy = '-';
            else
            {
                if(rand < 0.5)
                    signy = '+';
                else
                    signy = '-';
            }
        }
        else if(MainActivity.floor > -1 && MainActivity.floor < 8)
        {
            if(enemy.getPoint().getX() <= 1360)
                signx = '+';
            else if(enemy.getPoint().getX() >= 6924)
                signx = '-';
            else if(enemy.getPoint().getX() > 5467)
            {
                if(rand < 0.7)
                    signx = '-';
                else
                    signx = '+';
            }
            else if(enemy.getPoint().getX() < 3067)
            {
                if(rand < 0.7)
                    signx = '+';
                else
                    signx = '-';
            }
            else
            {
                if(rand < 0.5)
                    signx = '+';
                else
                    signx = '-';
            }

            if(enemy.getPoint().getY() <= 4280)
                signy = '+';
            else if(enemy.getPoint().getY() >= 4310)
                signy = '-';
            else
            {
                if(rand < 0.5)
                    signy = '+';
                else
                    signy = '-';
            }
        }

        if(signx == '+')
            pointX = ourObject.getPoint().getX() + 5;
        else
            pointX = ourObject.getPoint().getX() - 5;
        if(signy == '+')
            pointY = ourObject.getPoint().getY() + 5;
        else
            pointY = ourObject.getPoint().getY() - 5;
        ourObject.setPoint(new Point(pointX, pointY));
        drawingHelper.changePositionToPoint(ourObject, tileView);
    }
}
