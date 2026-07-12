package com.velas.candil.mappers;

import com.velas.candil.entities.ingredient.Ingredient;
import com.velas.candil.models.ingredient.IngredientRequestDto;
import com.velas.candil.models.ingredient.IngredientResponseDto;
import com.velas.candil.models.ingredient.IngredientUpdateDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pricePerUnit", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "candle", ignore = true)
    Ingredient toEntity(IngredientRequestDto dto);

    IngredientResponseDto toResponse(Ingredient ingredient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pricePerUnit", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "candle", ignore = true)
    void updateEntityFromDto(IngredientUpdateDto dto, @MappingTarget Ingredient entity);

    @AfterMapping
    default void updateDerivedFields(@MappingTarget Ingredient entity) {

        // If name changed, update dependent fields

        // Recalculate total price
        if (entity.getAmount() != null && entity.getPricePerUnit() != null) {
            entity.setPrice(entity.calculatePrice());
        }
    }
}