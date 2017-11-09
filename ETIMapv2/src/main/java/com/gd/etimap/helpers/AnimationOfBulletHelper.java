package com.gd.etimap.helpers;

import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.Bullet;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.Player;
import com.qozix.tileview.TileView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 02.11.2017.
 */
public class AnimationOfBulletHelper {

    public static volatile boolean isAnimationOfBullet = false;
    public static List<Point> listOfShootedPoints = new ArrayList<>();
    private DrawingHelper drawingHelper = new DrawingHelper();

    public void doAnimationOfBullet(ListOfAllObjects listOfAllObjects, double counter, TileView tileView){
        Player palyer = (Player) listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
        Bullet bullet = (Bullet) listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Bullet").get(0);
        bullet.setFloor(palyer.getFloor());

        if(counter == 0){
            try{
                bullet.setPoint(listOfShootedPoints.get((int) counter));
                drawingHelper.draw(bullet, tileView);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }else if(counter == (listOfShootedPoints.size()-1)){
            tileView.removeMarker(bullet.getMarker());
        }else {
            try{
                bullet.setPoint(listOfShootedPoints.get((int) counter));
                drawingHelper.changePositionToPoint(bullet, tileView);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
