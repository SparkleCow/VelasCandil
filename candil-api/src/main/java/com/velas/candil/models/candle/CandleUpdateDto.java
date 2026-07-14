package com.velas.candil.models.candle;

import com.velas.candil.models.ingredient.IngredientRequestDto;

import java.util.List;
import java.util.Set;

public record CandleUpdateDto(
        String name,
        String description,
        Set<MaterialEnum> materialEnums,
        Set<FeatureEnum> featureEnums,
        Set<CategoryEnum> categories,
        List<IngredientRequestDto> ingredients
) {}