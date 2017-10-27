package com.example.nataliasobolewska.androidapp.Services;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;

import com.example.nataliasobolewska.androidapp.Objects.ListOfAllObjects;
import com.example.nataliasobolewska.androidapp.Objects.OurObject;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import solid.stream.Stream;

/**
 * Created by Natalia Sobolewska on 17.10.2017.
 */

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class DrawingService {

    public static void drawEnemies(ListOfAllObjects listOfAllObjects){
        for(OurObject ourObject: listOfAllObjects.getListOfOurObjects()){
            ourObject.getImageView().setX(ourObject.getPoint().getX());
            ourObject.getImageView().setY(ourObject.getPoint().getY());
            ourObject.getImageView().setVisibility(View.VISIBLE);
        }
    }
}
