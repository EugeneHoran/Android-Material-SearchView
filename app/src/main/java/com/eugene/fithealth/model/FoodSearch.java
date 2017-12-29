
package com.eugene.fithealth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FoodSearch {

    @SerializedName("foods")
    @Expose
    private Foods foods;

    public Foods getFoods() {
        return foods;
    }

    public void setFoods(Foods foods) {
        this.foods = foods;
    }
}
