package com.gd.etimap.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.Enemy;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.qozix.tileview.TileView;

import java.util.ArrayList;
import java.util.List;

import solid.functions.Action1;
import solid.stream.Stream;

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
            if(removeAllDeadEnemies(listOfAllObjects, tileView)){
                changePictureOfEnemy(shootedEnemy, tileView);
                shootedEnemy.setHp(shootedEnemy.getHp() - 20);
            }
        }
    }

    private boolean removeAllDeadEnemies(ListOfAllObjects listOfAllObjects, TileView tileView){
        List<OurObject> listOfObjectsWithZeroHp = listOfAllObjects.findAllVisibleEnemiesWhichHpIsZero();
        Stream.stream(listOfObjectsWithZeroHp).forEach((Action1< OurObject>) e -> tileView.removeMarker(e.getMarker()));
        listOfAllObjects.getListOfOurObjects().removeAll(listOfObjectsWithZeroHp);

        return listOfObjectsWithZeroHp.isEmpty();
    }

    private OurObject isShooted(ListOfAllObjects listOfAllObjects){
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrArrowOrBullet("Player").get(0);
        List<Point> listOfShootedPoints = countListOfShootedPoints(player);
        List<Point> helperList = new ArrayList<>();
        List<OurObject> listOfAllVisibleEnemies = listOfAllObjects.findAllVisibleEnemies();

        double pXY = 0;
        double oXY = 0;
        OurObject objectToReturn = null;
        for(Point p: listOfShootedPoints){
            for(OurObject o: listOfAllVisibleEnemies){
                helperList.add(p);
                pXY = p.getX() + p.getY();
                oXY = o.getPoint().getX() + o.getPoint().getY();
                if(Math.abs(pXY - oXY) < 10){
                    AnimationOfBulletHelper.listOfShootedPoints.addAll(helperList);
                    objectToReturn = o;
                    break;
                }
            }
        }
        if(AnimationOfBulletHelper.listOfShootedPoints.isEmpty())
            AnimationOfBulletHelper.listOfShootedPoints = listOfShootedPoints;

        AnimationOfBulletHelper.isAnimationOfBullet = true;
        return objectToReturn;
    }

    private List<Point> countListOfShootedPoints(OurObject player){
        double degrees = player.getImageView().getRotation();
        List<Point> listOfShootedPoints = new ArrayList<>();
        double[] table;
        double counter = 0;

        if((degrees < -90 && degrees > -270) || (degrees > 90 && degrees < 270)){
            while(counter < 300){
                table = countPoint(counter, 10, player);
                counter = table[0];
                listOfShootedPoints.add(new Point(table[1], table[2]));
            }
            return listOfShootedPoints;
        }else{
            while(counter > -300){
                table = countPoint(counter, -10, player);
                counter = table[0];
                listOfShootedPoints.add(new Point(table[1], table[2]));
            }
            return listOfShootedPoints;
        }
    }

    private double[] countPoint(double counter, int number, OurObject player){
        double degrees = player.getImageView().getRotation();
        double radians = Math.toRadians(-degrees);
        double tangens = Math.tan(radians);
        double xPlayer = player.getPoint().getY();
        double yPlayer = player.getPoint().getX();
        double b = yPlayer - (tangens*xPlayer);
        counter += number;
        xPlayer += counter;
        yPlayer = tangens*xPlayer + b;

        return new double[]{counter, yPlayer, xPlayer};
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
}
