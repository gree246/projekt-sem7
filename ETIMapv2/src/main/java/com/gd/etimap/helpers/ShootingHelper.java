package com.gd.etimap.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.gd.etimap.MainActivity;
import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.Enemy;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.gd.etimap.objects.Player;
import com.qozix.tileview.TileView;

import java.util.ArrayList;
import java.util.List;

import solid.functions.Action1;
import solid.stream.Stream;

import static solid.collectors.ToList.toList;

/**
 * Created by Marcin on 29.10.2017.
 */

public class ShootingHelper {

    private static int eighty;
    private static int sixty;
    private static int fourty;
    private static int tweenty;
    private static int player1;
    private static int player2;
    private static int player3;
    private static int player4;
    private Context context;
    private ImageTransformationHelper imageTransformationHelper = new ImageTransformationHelper();
    private DrawingHelper drawingHelper = new DrawingHelper();

    public ShootingHelper(int resEighty, int resSixty, int resFourty, int resTweenty,
                          int player1, int player2, int player3, int player4, Context context) {
        eighty = resEighty;
        sixty = resSixty;
        fourty = resFourty;
        tweenty = resTweenty;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
        this.context = context;
    }

    public void shootToPlayer(ListOfAllObjects listOfAllObjects, TileView tileView){
        if(!AnimationOfBulletHelper.isAnimationOfBullet2) {
            List<Enemy> listOfAllEnemies = Stream.stream(listOfAllObjects.findAllVisibleEnemies()).map(o -> ((Enemy) o)).collect(toList());
            Enemy shooter = null;
            List<Point> listOfShootedPoints = null;

            if(!listOfAllEnemies.isEmpty()){
                if(listOfAllEnemies.size() == 2)
                    shooter = Math.random() > 0.5 ? listOfAllEnemies.get(1):listOfAllEnemies.get(0);
                else
                    shooter = listOfAllEnemies.get(0);

                OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);

                if(Math.abs(Math.abs(player.getPoint().getX()) - Math.abs(shooter.getPoint().getX())) < 450){
                     listOfShootedPoints = countListOfShootedPointsForEnemy(shooter, player.getPoint().getX(), player.getPoint().getY());
                    ((Player) player).setHp(((Player) player).getHp() - 20);
                    changePictureOfPlayer(player, tileView);
                }else{
                    double x = Math.random() < 0.5 ? 200:400;
                    x = Math.random() < 0.5 ? x:-x;
                    listOfShootedPoints = countListOfShootedPointsForEnemy(shooter, player.getPoint().getX()+x/2, player.getPoint().getY()+x);
                }

                if(AnimationOfBulletHelper.listOfShootedPoints2.isEmpty())
                    AnimationOfBulletHelper.listOfShootedPoints2 = listOfShootedPoints;
                AnimationOfBulletHelper.isAnimationOfBullet2 = true;

                /*if(((Player) player).getHp() <= 0)
                endGame();*/
            }

        }
    }

    private List<Point> countListOfShootedPointsForEnemy(Enemy shooter, double x1, double y1){
        double x2 = shooter.getPoint().getX();
        double y2 = shooter.getPoint().getY();
        List<Point> list = new ArrayList<>();

        double x = (x1-x2)/20;
        for(int i=0;i<20;i++){
            list.add(countOnePointForEnemy(x2, x1, y2, y1, x2+x*i));
        }
        return list;
    }

    private Point countOnePointForEnemy(double x1, double x2, double y1, double y2, double x){
        return new Point(x, (((y2-y1)*(x-x1)/(x2-x1)) + y1));
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
                if((Math.abs(rX) + Math.abs(rY)) < 55){
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

        if(degrees == 90)
            return countFor90(listOfShootedPoints, "+", player);
        if(degrees == -90)
            return countFor90(listOfShootedPoints, "-", player);

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

    private List<Point> countFor90(List<Point> listOfShootedPoints, String sign, OurObject player){
        int mul = sign.equals("+")?10:-10;
        double x = player.getPoint().getX();
        for(int i=0;i<30;i++){
            x += mul;
            listOfShootedPoints.add(new Point(x,player.getPoint().getY()));
        }
        return listOfShootedPoints;
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
        ImageView newImage = imageTransformationHelper.createImageView(eighty, context, false);
        if(hp <= 80 && hp > 60){
            newImage = imageTransformationHelper.createImageView(eighty, context, false);
        }else if(hp <= 60 && hp > 40){
            newImage = imageTransformationHelper.createImageView(sixty, context, false);
        }else if(hp <= 40 && hp > 20){
            newImage = imageTransformationHelper.createImageView(fourty, context, false);
        }else if(hp <= 20){
            newImage = imageTransformationHelper.createImageView(tweenty, context, false);
        }
        newImage.setScaleX(MainActivity.scaleOfAvatars);
        newImage.setScaleY(MainActivity.scaleOfAvatars);
        ourObject.setImageView(newImage);
        return (Enemy) ourObject;
    }

    private OurObject changePictureOfPlayer(OurObject player, TileView tileView){
        float rotation = player.getMarker().getRotation();
        tileView.removeMarker(player.getMarker());

        int hp = ((Player) player).getHp();
        if(hp <= 400 && hp > 300){
            player.setImageView(imageTransformationHelper.createImageView(player1, context, false));
        }else if(hp <= 300 && hp > 200){
            player.setImageView(imageTransformationHelper.createImageView(player2, context, false));
        }else if(hp <= 200 && hp > 100){
            player.setImageView(imageTransformationHelper.createImageView(player3, context, false));
        }else if(hp <= 100){
            player.setImageView(imageTransformationHelper.createImageView(player4, context, false));
        }

        player.getImageView().setScaleX(MainActivity.scaleOfAvatars);
        player.getImageView().setScaleY(MainActivity.scaleOfAvatars);
        player.getImageView().setRotation(rotation);
        drawingHelper.draw(player, tileView);

        return (Player) player;
    }
}
