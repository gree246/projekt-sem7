package com.example.nataliasobolewska.androidapp.Objects;

import android.widget.ImageView;

import com.example.nataliasobolewska.androidapp.Atributtes.Point;

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

    public OurObject findObjectByName(String name){
        try{
            return Stream.stream(this.listOfOurObjects).filter(r -> filterByName(r, name)).collect(toList()).get(0);
        }catch(IndexOutOfBoundsException e){
            return new OurObject();
        }
    }

    private boolean filterByName(OurObject ourObject, String name){
        return ourObject.getName().equalsIgnoreCase(name);
    }

    public void createObjects(Point point, ImageView imageView, ImageView imageView2){
        listOfOurObjects.add(new OurObject("1", point, "1", imageView));
        listOfOurObjects.add(new OurObject("2", new Point(point.getX() + 20, point.getY() + 20), "1", imageView2));
    }

    public void removeAllObjects(){
        listOfOurObjects.clear();
    }
}
