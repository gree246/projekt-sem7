package com.gd.etimap.helpers;

import android.content.Context;

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

    private static int eighty;
    private static int sixty;
    private static int fourty;
    private static int tweenty;
    private Context context;
    private ImageTransformationHelper imageTransformationHelper = new ImageTransformationHelper();
    private DrawingHelper drawingHelper = new DrawingHelper();

    public ShootingHelper(int resEighty, int resSixty, int resFourty, int resTweenty, Context context) {
        eighty = resEighty;
        sixty = resSixty;
        fourty = resFourty;
        tweenty = resTweenty;
        this.context = context;
    }

    public void shoot(ListOfAllObjects listOfAllObjects, TileView tileView){
        if(!AnimationOfBulletHelper.isAnimationOfBullet){
            Enemy shootedEnemy = (Enemy) isShooted(listOfAllObjects);
            if(shootedEnemy != null){
                shootedEnemy.setHp(shootedEnemy.getHp() - 20);
                if(removeAllDeadEnemies(listOfAllObjects, tileView))
                    changePictureOfEnemy(shootedEnemy, tileView);
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
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
        List<Point> listOfShootedPoints = countListOfShootedPoints(player);
        List<Point> helperList = new ArrayList<>();
        List<OurObject> listOfAllVisibleEnemies = listOfAllObjects.findAllVisibleEnemies();

        double rX = 0;
        double rY = 0;
        OurObject objectToReturn = null;
        breakFromLoop:
        for(Point p: listOfShootedPoints){
            helperList.add(p);
            for(OurObject o: listOfAllVisibleEnemies){
                rX = p.getX() - o.getPoint().getX();
                rY = p.getY() - o.getPoint().getY();
                if((Math.abs(rX) + Math.abs(rY)) < 33){
                    AnimationOfBulletHelper.listOfShootedPoints = helperList;
                    objectToReturn = o;
                    break breakFromLoop;
                }
            }
        }
        if(AnimationOfBulletHelper.listOfShootedPoints.isEmpty())
            AnimationOfBulletHelper.listOfShootedPoints = listOfShootedPoints;

        AnimationOfBulletHelper.isAnimationOfBullet = true;
        return objectToReturn;
    }

    private List<Point> countListOfShootedPoints(OurObject player){
        double degrees = countDeegres(player.getImageView().getRotation());
        List<Point> listOfShootedPoints = new ArrayList<>();
        double[] table;
        double counter = 0;

        if((degrees < -90 && degrees > -270) || (degrees > 90 && degrees < 270)){
            while(counter < 300){
                table = countPoint(counter, 10, player, degrees);
                counter = table[0];
                listOfShootedPoints.add(new Point(table[1], table[2]));
            }
            return listOfShootedPoints;
        }else{
            while(counter > -300){
                table = countPoint(counter, -10, player, degrees);
                counter = table[0];
                listOfShootedPoints.add(new Point(table[1], table[2]));
            }
            return listOfShootedPoints;
        }
    }
    private double countDeegres(double degrees){
        int mul = ((int) degrees) / 360;
        return degrees - mul*360;
    }

    private double[] countPoint(double counter, int number, OurObject player, double degrees){
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
        Enemy enemy = (Enemy) ourObject;
        tileView.removeMarker(enemy.getMarker());
        enemy = changePicture(enemy);
        enemy.setPoint(new Point(enemy.getPoint().getX() + 10, enemy.getPoint().getY() + 10));
        drawingHelper.draw(enemy, tileView);
    }

    private Enemy changePicture(OurObject ourObject){
        int hp = ((Enemy) ourObject).getHp();
        if(hp <= 80 && hp > 60){
            ourObject.setImageView(imageTransformationHelper.createImageView(eighty, context, false));
        }else if(hp <= 60 && hp > 40){
            ourObject.setImageView(imageTransformationHelper.createImageView(sixty, context, false));
        }else if(hp <= 40 && hp > 20){
            ourObject.setImageView(imageTransformationHelper.createImageView(fourty, context, false));
        }else if(hp <= 20){
            ourObject.setImageView(imageTransformationHelper.createImageView(tweenty, context, false));
        }
        return (Enemy) ourObject;
    }
}
