package com.velas.candil.models.ingredient;

import lombok.Getter;

@Getter
public enum IngredientUnit {

    GRAM("g"),
    MILLILITER("ml"),
    UNIT("unit");

    private final String symbol;

    IngredientUnit(String symbol) {
        this.symbol = symbol;
    }
}