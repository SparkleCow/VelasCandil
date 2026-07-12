package com.velas.candil.models.ingredient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record IngredientRequestDto(
        @NotNull
        Long ingredientId,

        @NotNull
        @Positive
        BigDecimal amount
) {}