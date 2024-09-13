package ru.plumsoftware.data.model.ui;

import androidx.annotation.DrawableRes;

public class Shape {
    @DrawableRes private int shapeRes;

    public Shape(int shapeRes) {
        this.shapeRes = shapeRes;
    }

    public int getShapeRes() {
        return shapeRes;
    }
}
