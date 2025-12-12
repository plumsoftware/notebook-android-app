package ru.plumsoftware.data.model.ui;

import androidx.annotation.ColorRes;

public class Colors {
    @ColorRes private final int colorRes;

    public Colors(int colorRes) {
        this.colorRes = colorRes;
    }

    public int getColorRes() {
        return colorRes;
    }
}
