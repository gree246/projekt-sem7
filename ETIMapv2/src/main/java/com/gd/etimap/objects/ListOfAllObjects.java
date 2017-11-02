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
    private List<OurObject> listOfOurObjects;

    public ListOfAllObjects() {
        listOfOurObjects = new ArrayList<>();
    }

    public List<OurObject> findAllEnemiesOrPlayerOrArrowOrBullet(String name){
        if(name.equalsIgnoreCase("Enemy"))
            return Stream.stream(this.listOfOurObjects).filter(r -> filterEnemies(r)).collect(toList());
        if(name.equalsIgnoreCase("Arrow"))
            return Stream.stream(this.listOfOurObjects).filter(r -> filterArrow(r)).collect(toList());
        if(name.equalsIgnoreCase("Bullet"))
            return Stream.stream(this.listOfOurObjects).filter(r -> filterBullet(r)).collect(toList());
        return Stream.stream(this.listOfOurObjects).filter(r -> filterPlayer(r)).collect(toList());
    }

    public List<OurObject> findAllVisibleEnemiesWhichHpIsZero(){
        return Stream.stream(findAllVisibleEnemies()).filter(e -> filterHp(e)).collect(toList());
    }

    public List<OurObject> findAllVisibleEnemies(){
        return Stream.stream(findAllEnemiesOrPlayerOrArrowOrBullet("Enemy")).filter(e -> filterVisible(e)).collect(toList());
    }
    public List<OurObject> findAllUnVisibleEnemies(){
        return Stream.stream(findAllEnemiesOrPlayerOrArrowOrBullet("Enemy")).filter(e -> filterUnVisible(e)).collect(toList());
    }

    public OurObject findObjectById(int id){
        return Stream.stream(this.listOfOurObjects).filter(e -> filterById(e, id)).collect(toList()).get(0);
    }

    private boolean filterBullet(OurObject ourObject){
        return ourObject instanceof Bullet;
    }

    private boolean filterById(OurObject ourObject, int id){
        return ourObject.getId() == id;
    }

    private boolean filterHp(OurObject ourObject){
        return ((Enemy) ourObject).getHp() == 0;
    }

    public static boolean filterVisible(OurObject ourObject){
        return !filterUnVisible(ourObject);
    }

    private static boolean filterUnVisible(OurObject ourObject){
        return !((Enemy) ourObject).isVisible();
    }

    private boolean filterArrow(OurObject ourObject){
        return ourObject instanceof Arrow;
    }

    private boolean filterEnemies(OurObject ourObject){
        return ourObject instanceof Enemy;
    }

    private boolean filterPlayer(OurObject ourObject){
        return ourObject instanceof Player;
    }

    public void createBullet(ImageView imageView){
        listOfOurObjects.add(new Bullet(new Point(0, 0), "1", imageView, null));
    }

    public void createArrow(ImageView imageView){
      listOfOurObjects.add(new Arrow(new Point(0, 0), "1", imageView, null));
    }

    public void createEnemy(Point point, ImageView imageView){
        listOfOurObjects.add(new Enemy(point, "1", imageView, false, 100, null));
    }

    public void createPlayer(Point point, ImageView imageView){
        listOfOurObjects.add(new Player(point, "1", imageView, null));
    }

    public void removeAllObjects(){
        listOfOurObjects.clear();
    }
}
