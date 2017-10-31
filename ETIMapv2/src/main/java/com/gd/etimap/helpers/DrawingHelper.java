package com.gd.etimap.helpers;

import android.view.View;
import android.widget.ImageView;

import com.gd.etimap.atributtes.Point;
import com.gd.etimap.objects.Arrow;
import com.gd.etimap.objects.Enemy;
import com.gd.etimap.objects.ListOfAllObjects;
import com.gd.etimap.objects.OurObject;
import com.gd.etimap.objects.Player;
import com.qozix.tileview.TileView;

import solid.functions.Action1;
import solid.stream.Stream;

/**
 * Created by Marcin on 28.10.2017.
 */

public class DrawingHelper {

    public void drawPlayerAndArrowObjects(ListOfAllObjects listOfAllObjects, TileView tileView){
        Stream.stream(listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player")).forEach((Action1<OurObject>) o -> draw(o, tileView));
        drawArrow(listOfAllObjects, tileView);
    }

    public void draw(OurObject ourObject, TileView tileView){
        View marker = tileView.addMarker(ourObject.getImageView(),ourObject.getPoint().getX(),ourObject.getPoint().getY(),-0.5f,-0.5f);
        ourObject.setMarker(marker);
        if(ourObject instanceof Enemy)
            ((Enemy) ourObject).setVisible(true);
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
        Player player = (Player) listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player").get(0);
        Arrow arrow = (Arrow) listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Arrow").get(0);
        changePositionOfObject(player, xOrY, minusOrPlus, tileView);
        changePositionOfObject(arrow, xOrY, minusOrPlus, tileView);
    }

    public void drawEnemy(ListOfAllObjects listOfAllObjects, ImageView imageView, TileView tileView){
        if(listOfAllObjects.findAllVisibleEnemies().size() < 2){
            OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player").get(0);
            Point point = randPosition();
            listOfAllObjects.createEnemy(new Point(player.getPoint().getX() + point.getX(), player.getPoint().getY() + point.getY()), imageView);
            draw(listOfAllObjects.findAllUnVisibleEnemies().get(0), tileView);
        }
    }

    private void drawArrow(ListOfAllObjects listOfAllObjects, TileView tileView){
        OurObject arrow = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Arrow").get(0);
        OurObject player = listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Player").get(0);

        arrow.setPoint(new Point(player.getPoint().getX(), player.getPoint().getY()));
        draw(arrow, tileView);
    }

    private void removeOldEnemiesFromMap(ListOfAllObjects listOfAllObjects, TileView tileView){
        Stream.stream(listOfAllObjects.findAllEnemiesOrPlayerOrArrow("Enemy"))
                .filter(ListOfAllObjects::filterVisible)
                .forEach((Action1<OurObject>) o -> tileView.removeMarker(o.getMarker()));
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
