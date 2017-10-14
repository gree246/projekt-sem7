package com.example.nataliasobolewska.androidapp.Objects;

import com.example.nataliasobolewska.androidapp.Atributtes.Point;
import com.example.nataliasobolewska.androidapp.Atributtes.Position;

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

    public void createObjects(Position position){
        listOfOurObjects.add(new OurObject("1", 0xFFFF0000, position, "1"));
        listOfOurObjects.add(new OurObject("2", 0xAAAA1111, new Position(new Point(position.getDownPoint().getX() + 20, position.getDownPoint().getY() + 10), new Point(position.getUpPoint().getX() + 20, position.getUpPoint().getY() + 10)), "2"));

        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            TUTRAJ MEJSCE NA DODANIE TWOJEJ KULKI CZY CZEGOS TAM
        */
    }

    public void removeAllObjects(){
        listOfOurObjects.clear();
    }
}
