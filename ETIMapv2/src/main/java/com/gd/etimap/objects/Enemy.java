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
public class Enemy extends OurObject{
    private int hp;
    private boolean visible;

    public Enemy(Point point, String floor, ImageView imageView,boolean visible, int hp, View marker) {
        super(point, floor, imageView, marker);
        this.hp = hp;
        this.visible = visible;
    }
}
