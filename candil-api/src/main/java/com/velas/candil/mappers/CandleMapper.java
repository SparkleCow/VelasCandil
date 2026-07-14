package com.velas.candil.mappers;

import com.velas.candil.entities.candle.Candle;
import com.velas.candil.models.candle.CandleRequestDto;
import com.velas.candil.models.candle.CandleResponseDto;
import com.velas.candil.models.candle.CandleUpdateDto;
import org.mapstruct.*;

import java.util.Collection;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = IngredientMapper.class
)
public interface CandleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    Candle toEntity(CandleRequestDto dto);

    CandleResponseDto toResponse(Candle candle);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "principalImage", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "manufacturingCost", ignore = true)
    @Mapping(target = "profit", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "stock", ignore = true)
    void updateEntityFromDto(CandleUpdateDto dto, @MappingTarget Candle candle);

    @Condition
    default <T> boolean isNotEmpty(Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }

    @Condition
    default boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}