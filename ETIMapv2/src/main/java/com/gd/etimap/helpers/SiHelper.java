package com.gd.etimap.helpers;

import com.gd.etimap.MainActivity;
import com.gd.etimap.atributtes.EnemyAnimation;
import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.Enemy;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.gd.etimap.objects.Player;
import com.qozix.tileview.TileView;

import com.gd.etimap.helpers.ShootingHelper;

import java.util.List;

import solid.functions.Action1;
import solid.stream.Stream;

import static solid.collectors.ToList.toList;

/**
 * Created by Marcin on 09.11.2017.
 */

public class SiHelper {
    double playerX, playerY;
    DrawingHelper drawingHelper = new DrawingHelper();


    public void doEnemySi(ListOfAllObjects listOfAllObjects, TileView tileView, ShootingHelper shootingHelper){
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
        playerX = player.getPoint().getX();
        playerY = player.getPoint().getY();
        List<OurObject> listOfVisibleEnemies = listOfAllObjects.findAllVisibleEnemies();
        List<OurObject> listOfNewVisibleEnemies = getNewEnemies(listOfVisibleEnemies);
        List<OurObject> listOfOldVisibleEnemies = getOldEnemies(listOfVisibleEnemies);

//        Stream.stream(listOfNewVisibleEnemies).forEach((Action1<OurObject>) o -> changePositionOfNewEnemies(o, player));
//        Stream.stream(listOfOldVisibleEnemies).forEach((Action1<OurObject>) o -> changePositionOfOldEnemies(o, tileView));


        Stream.stream(listOfVisibleEnemies).forEach((Action1<OurObject>) o -> changePositionOfOldEnemies(o, tileView,player,shootingHelper));
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

    private void changePositionOfOldEnemies(OurObject ourObject, TileView tileView, OurObject player, ShootingHelper shootingHelper){
        Enemy enemy = ((Enemy) ourObject);
        double rand = Math.random();
        char signx, signy;
        double pointX, pointY, diffX, diffY;

        diffX = enemy.getPoint().getX() - playerX;
        diffY = enemy.getPoint().getY() - playerY;

        pointX = 0;
        pointY = 0;

        int speed = 5;

        signx = '+';
        signy = '+';


        if(MainActivity.floor == -1)
        {
            if(enemy.getPoint().getX()- playerX > 0)
                signx = '-';
            else
                signx = '+';

            if(enemy.getPoint().getY() <= 4228)
                signy = '+';
            else if(enemy.getPoint().getY() >= 4278)
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
            if(enemy.getPoint().getX()- playerX > 0)
                signx = '-';
            else
                signx = '+';

            if(enemy.getPoint().getY() <= 4270)
                signy = '+';
            else if(enemy.getPoint().getY() >= 4320)
                signy = '-';
            else
            {
                if(rand < 0.5)
                    signy = '+';
                else
                    signy = '-';
            }
        }

        if(Math.abs(diffX)<60 && Math.abs(diffY)<60)
        {
            ((Player) player).setHp(((Player) player).getHp() - 6);
            shootingHelper.changePictureOfPlayer(player, tileView);
            if(((Player) player).getHp() <= 0)
                shootingHelper.theEnd = true;
        }

        if(signx == '+')
            pointX = ourObject.getPoint().getX() + speed;
        else
            pointX = ourObject.getPoint().getX() - speed;

        if(signy == '+')
            pointY = ourObject.getPoint().getY() + speed;
        else
            pointY = ourObject.getPoint().getY() - speed;
        ourObject.setPoint(new Point(pointX, pointY));
        drawingHelper.changePositionToPoint(ourObject, tileView);
    }


}
