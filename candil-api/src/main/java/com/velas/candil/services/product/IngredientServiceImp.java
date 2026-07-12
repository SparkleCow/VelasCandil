package com.velas.candil.services.product;

import com.velas.candil.entities.ingredient.Ingredient;
import com.velas.candil.mappers.IngredientMapper;
import com.velas.candil.models.ingredient.IngredientRequestDto;
import com.velas.candil.models.ingredient.IngredientResponseDto;
import com.velas.candil.models.ingredient.IngredientType;
import com.velas.candil.models.ingredient.IngredientUpdateDto;
import com.velas.candil.repositories.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngredientServiceImp implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Override
    public IngredientResponseDto create(IngredientRequestDto ingredientRequestDto) {

        Ingredient ingredient = ingredientMapper.toEntity(ingredientRequestDto);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        log.info("Ingredient created successfully. ID: {}", savedIngredient.getId());
        return ingredientMapper.toResponse(savedIngredient);
    }

    @Override
    public Page<IngredientResponseDto> findAll(Pageable pageable) {
        log.info("Fetching all ingredients. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return ingredientRepository.findAll(pageable)
                .map(ingredientMapper::toResponse);
    }

    @Override
    public IngredientResponseDto findById(Long id) {

        log.info("Searching ingredient by ID: {}", id);
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ingredient not found with ID: {}", id);
                    return new EntityNotFoundException("Ingredient not found with id: " + id);
                });

        return ingredientMapper.toResponse(ingredient);
    }

    @Transactional
    @Override
    public IngredientResponseDto update(IngredientUpdateDto ingredientUpdateDto, Long id) {

        log.info("Updating ingredient with ID: {}", id);

        Ingredient existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update. Ingredient not found with ID: {}", id);
                    return new EntityNotFoundException("Ingredient not found with id: " + id);
                });

        ingredientMapper.updateEntityFromDto(ingredientUpdateDto, existingIngredient);

        Ingredient updatedIngredient = ingredientRepository.save(existingIngredient);

        log.info("Ingredient updated successfully. ID: {}", updatedIngredient.getId());

        return ingredientMapper.toResponse(updatedIngredient);
    }

    @Override
    public void delete(Long id) {

        log.info("Deleting ingredient with ID: {}", id);

        if (!ingredientRepository.existsById(id)) {
            log.error("Cannot delete. Ingredient not found with ID: {}", id);
            throw new EntityNotFoundException("Ingredient not found with id: " + id);
        }

        ingredientRepository.deleteById(id);

        log.info("Ingredient deleted successfully. ID: {}", id);
    }

    @Override
    public Page<IngredientResponseDto> findAllById(List<Long> ids) {
        return null;
    }
}