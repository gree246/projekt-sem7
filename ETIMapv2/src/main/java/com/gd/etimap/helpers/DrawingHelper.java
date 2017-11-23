package com.gd.etimap.helpers;

import android.view.View;
import android.widget.ImageView;

import com.gd.etimap.MainActivity;
import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.Enemy;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.gd.etimap.objects.Player;
import com.qozix.tileview.TileView;
import com.qozix.tileview.detail.DetailLevel;

import solid.functions.Action1;
import solid.stream.Stream;

/**
 * Created by Marcin on 28.10.2017.
 */

public class DrawingHelper {

    public void drawPlayer(ListOfAllObjects listOfAllObjects, TileView tileView){
        Stream.stream(listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player")).forEach((Action1<OurObject>) o -> draw(o, tileView));
    }

    public void draw(OurObject ourObject, TileView tileView){
        View marker = tileView.addMarker(ourObject.getImageView(),ourObject.getPoint().getX(),ourObject.getPoint().getY(),-0.5f,-0.5f);
        ourObject.setMarker(marker);
        if(ourObject instanceof Enemy && !((Enemy) ourObject).isVisible())
            ((Enemy) ourObject).setVisible(true);
    }

    public void changePositionToPoint(OurObject ourObject, TileView tileView){
        tileView.moveMarker(ourObject.getMarker(), ourObject.getPoint().getX(), ourObject.getPoint().getY());
    }

    public void changePositionOfObject(OurObject ourObject, String xOrY, String minusOrPlus, TileView tileView){
        if(xOrY.equals("x") && minusOrPlus.equals("-")){
            ourObject.getPoint().setX(ourObject.getPoint().getX() - 6);
        }else if(xOrY.equals("x") && minusOrPlus.equals("+")){
            ourObject.getPoint().setX(ourObject.getPoint().getX() + 6);
        }else if(xOrY.equals("y") && minusOrPlus.equals("-")){
            ourObject.getPoint().setY(ourObject.getPoint().getY() - 6);
        }else if(xOrY.equals("y") && minusOrPlus.equals("+")) {
            ourObject.getPoint().setY(ourObject.getPoint().getY() + 6);
        }
        double x = ourObject.getPoint().getX();
        double y = ourObject.getPoint().getY();

        tileView.moveMarker(ourObject.getMarker(), x, y);
    }

    public void changePositionOfPlayer(ListOfAllObjects listOfAllObjects, String xOrY, String minusOrPlus, TileView tileView){
        Player player = (Player) listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
        changePositionOfObject(player, xOrY, minusOrPlus, tileView);
    }

    public void drawEnemy(ListOfAllObjects listOfAllObjects, ImageView imageView, TileView tileView){
        if(listOfAllObjects.findAllVisibleEnemies().size() < 2){
            OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrBullet("Player").get(0);
            Point point = randPosition();
            listOfAllObjects.createEnemy(new Point(player.getPoint().getX() + point.getX(), player.getPoint().getY() + point.getY()), imageView);
            draw(listOfAllObjects.findAllUnVisibleEnemies().get(0), tileView);
        }
    }

    public static void changeFloorUp(TileView tileView)
    {
        MainActivity.floor += 1;
        if(MainActivity.floor > 9)
        {
            MainActivity.floor = 9;
        }
        DetailLevel detLev = new DetailLevel(tileView.getDetailLevelManager(),1f, "floor"+MainActivity.floor+"/tile_"+MainActivity.floor+"_%d_%d.png", 256, 256);
        tileView.onDetailLevelChanged(detLev);
    }

    public static void changeFloorDown(TileView tileView)
    {
        MainActivity.floor -= 1;
        if(MainActivity.floor < (-1))
        {
            MainActivity.floor = -1;
        }
        DetailLevel detLev = new DetailLevel(tileView.getDetailLevelManager(),1f, "floor"+MainActivity.floor+"/tile_"+MainActivity.floor+"_%d_%d.png", 256, 256);
        tileView.onDetailLevelChanged(detLev);
    }

    public void changeFloor(TileView tileView, int floor)
    {
        MainActivity.floor = floor;
        if(MainActivity.floor < (-1)) {
            MainActivity.floor = -1;
        }else if(MainActivity.floor > 9) {
            MainActivity.floor = 9;
        }
        DetailLevel detLev = new DetailLevel(tileView.getDetailLevelManager(),1f, "floor"+MainActivity.floor+"/tile_"+MainActivity.floor+"_%d_%d.png", 256, 256);
        tileView.onDetailLevelChanged(detLev);
    }

    private Point randPosition(){
        double rand = Math.random();

        if(rand < 0.25)
            return new Point(150, 170);
        if(rand > 0.25 && rand < 0.5)
            return new Point(-150, 170);
        if(rand > 0.5 && rand < 0.75)
            return new Point(150, -170);
        if(rand > 0.75)
            return new Point(-150, -170);
        return null;
    }
}
