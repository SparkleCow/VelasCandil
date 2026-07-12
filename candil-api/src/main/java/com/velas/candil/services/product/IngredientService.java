package com.velas.candil.services.product;

import com.velas.candil.models.ingredient.IngredientRequestDto;
import com.velas.candil.models.ingredient.IngredientResponseDto;
import com.velas.candil.models.ingredient.IngredientType;
import com.velas.candil.models.ingredient.IngredientUpdateDto;
import com.velas.candil.services.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IngredientService extends CrudService<IngredientRequestDto, IngredientResponseDto, IngredientUpdateDto, Long> {

    Page<IngredientResponseDto> findAll(Pageable pageable);
    Page<IngredientResponseDto> findAllById(List<Long> ids);
}
