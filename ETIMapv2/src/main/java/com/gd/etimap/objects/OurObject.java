package com.gd.etimap.objects;


import android.view.View;
import android.widget.ImageView;

import com.gd.etimap.atributtes.Point;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class OurObject {
    private Point point;
    private String floor;
    private ImageView imageView;
    private View marker;
}
