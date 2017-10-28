package com.gd.etimap.objects;

import android.widget.ImageView;

import com.gd.etimap.atributtes.Point;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import solid.stream.Stream;

import static solid.collectors.ToList.toList;

/**
 * Created by Marcin on 10.10.2017.
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ListOfAllObjects {
    private List<OurObject> listOfOurOurObjects;

    public ListOfAllObjects() {
        listOfOurOurObjects = new ArrayList<>();
    }

    public List<OurObject> findAllEnemiesOrPlayer(String name){
        if(name.equalsIgnoreCase("Enemy"))
            return Stream.stream(this.listOfOurOurObjects).filter(r -> filterEnemies(r)).collect(toList());

        return Stream.stream(this.listOfOurOurObjects).filter(r -> filterPlayer(r)).collect(toList());
    }

    public List<OurObject> findAllUnVisibleEnemies(){
        return Stream.stream(findAllEnemiesOrPlayer("Enemy")).filter(e -> filterUnVisible(e)).collect(toList());
    }

    public static boolean filterVisible(OurObject ourObject){
        return !filterUnVisible(ourObject);
    }
    private static boolean filterUnVisible(OurObject ourObject){
        return !((Enemy) ourObject).isVisible();
    }

    private boolean filterEnemies(OurObject ourObject){
        return ourObject instanceof Enemy;
    }

    private boolean filterPlayer(OurObject ourObject){
        return ourObject instanceof Player;
    }

    public void createEnemy(Point point, ImageView imageView){
        listOfOurOurObjects.add(new Enemy(point, "1", imageView, false, 100, null));
    }

    public void createPlayer(Point point, ImageView imageView){
        listOfOurOurObjects.add(new Player(point, "1", imageView, null));
    }

    public void removeAllObjects(){
        listOfOurOurObjects.clear();
    }
}
