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
 * Created by Marcin on 28.10.2017.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Player extends OurObject{

    private int hp;

    public Player(Point point, String floor, ImageView imageView, View marker, int hp) {
        super(point, floor, imageView, marker);
        this.hp = hp;
    }
}
