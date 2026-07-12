package com.velas.candil.models.ingredient;

import lombok.Getter;

@Getter
public enum IngredientUnit {

    GRAM("GRAM"),
    MILLILITER("ML"),
    UNIT("UNIT");

    private final String symbol;

    IngredientUnit(String symbol) {
        this.symbol = symbol;
    }

    public static IngredientUnit fromSymbol(String symbol) {

        for (IngredientUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol)) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Unknown unit: " + symbol);
    }
}