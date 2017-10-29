package com.gd.etimap.objects;


import android.view.View;
import android.widget.ImageView;

import com.gd.etimap.atributtes.Point;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Marcin on 10.10.2017.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class OurObject {
    private static int id = 0;
    private Point point;
    private String floor;
    private ImageView imageView;
    private View marker;

    public OurObject(Point point, String floor, ImageView imageView, View marker) {
        this.id++;
        this.point = point;
        this.floor = floor;
        this.imageView = imageView;
        this.marker = marker;
    }

    public static int getId() {
        return id;
    }
}
