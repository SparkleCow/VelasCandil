package com.velas.candil.mappers;

import com.velas.candil.entities.candle.Candle;
import com.velas.candil.models.candle.CandleRequestDto;
import com.velas.candil.models.candle.CandleResponseDto;
import com.velas.candil.models.candle.CandleUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    void updateEntityFromDto(CandleUpdateDto dto, @MappingTarget Candle candle);
}