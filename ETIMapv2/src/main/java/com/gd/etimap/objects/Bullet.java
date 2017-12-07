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
 * Created by Marcin on 02.11.2017.
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Bullet extends OurObject{

    boolean isForEnemy;

    public Bullet(Point point, String floor, ImageView imageView, View marker, boolean isForEnemy) {
        super(point, floor, imageView, marker);
        this.isForEnemy = isForEnemy;
    }
}
