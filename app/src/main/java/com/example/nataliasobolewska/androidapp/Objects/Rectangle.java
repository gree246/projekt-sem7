package com.example.nataliasobolewska.androidapp.Objects;


import com.example.nataliasobolewska.androidapp.Atributtes.Position;

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
public class Rectangle {
    private String name;
    private int color;
    private Position Position;
    private String floor;

}
