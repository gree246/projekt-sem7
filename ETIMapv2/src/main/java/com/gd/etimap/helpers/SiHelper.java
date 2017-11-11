package com.gd.etimap.helpers;

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

        Stream.stream(listOfNewVisibleEnemies).forEach((Action1<OurObject>) o -> changePositionOfNewEnemies(o, player));
        Stream.stream(listOfOldVisibleEnemies).forEach((Action1<OurObject>) o -> changePositionOfOldEnemies(o, tileView));
    }

    private List<OurObject> getNewEnemies(List<OurObject> listOfVisibleEnemies){
        return Stream.stream(listOfVisibleEnemies).filter(e -> !((Enemy) e).isSi()).collect(toList());
    }

    private List<OurObject> getOldEnemies(List<OurObject> listOfVisibleEnemies){
        return Stream.stream(listOfVisibleEnemies).filter(e -> ((Enemy) e).isSi()).collect(toList());
    }

    private void changePositionOfNewEnemies(OurObject ourObject, OurObject player){
        if (player.getPoint().getX() > ourObject.getPoint().getX()){
            ((Enemy) ourObject).setEnemyAnimation(new EnemyAnimation(true, 0));
        }else{
            ((Enemy) ourObject).setEnemyAnimation(new EnemyAnimation(false, 0));
        }
        ((Enemy) ourObject).setSi(true);
    }

    private void changePositionOfOldEnemies(OurObject ourObject, TileView tileView){
        Enemy enemy = ((Enemy) ourObject);
        if(enemy.getEnemyAnimation().isDown())
            ourObject.setPoint(new Point(ourObject.getPoint().getX() + 10, ourObject.getPoint().getY()));
        else
            ourObject.setPoint(new Point(ourObject.getPoint().getX() - 10, ourObject.getPoint().getY()));

        if(enemy.getEnemyAnimation().getTime() >= 20){
            enemy.getEnemyAnimation().setTime(0);
            enemy.getEnemyAnimation().setDown(!enemy.getEnemyAnimation().isDown());
        }
        enemy.getEnemyAnimation().setTime(enemy.getEnemyAnimation().getTime() + 1);
        drawingHelper.changePositionToPoint(ourObject, tileView);
    }
}
