package com.example.nataliasobolewska.androidapp.Objects;


import android.widget.ImageView;

import com.example.nataliasobolewska.androidapp.Atributtes.Point;

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class OurObject {
    private String name;
    private Point point;
    private String floor;
    private ImageView imageView;
}
