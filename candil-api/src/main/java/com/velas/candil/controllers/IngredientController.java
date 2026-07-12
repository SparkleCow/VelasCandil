package com.velas.candil.controllers;

import com.velas.candil.models.ingredient.IngredientRequestDto;
import com.velas.candil.models.ingredient.IngredientResponseDto;
import com.velas.candil.models.ingredient.IngredientType;
import com.velas.candil.models.ingredient.IngredientUpdateDto;
import com.velas.candil.services.product.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ingredients")
@Tag(name = "Ingredients", description = "Operations related to candle ingredients management")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(
            summary = "Create ingredient",
            description = "Creates a new ingredient in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient created successfully"),
            @ApiResponse(responseCode = "400", description = "Malformed request")
    })
    @PostMapping
    public ResponseEntity<IngredientResponseDto> create(
            @Parameter(description = "Ingredient information", required = true)
            @RequestBody @Valid IngredientRequestDto dto
    ) {
        return ResponseEntity.ok(ingredientService.create(dto));
    }

    @Operation(
            summary = "Get all ingredients",
            description = "Returns a paginated list of ingredients."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<IngredientResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ingredientService.findAll(pageable));
    }

    @Operation(
            summary = "Get ingredient by ID",
            description = "Returns a specific ingredient by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient found"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponseDto> findById(
            @Parameter(description = "Ingredient ID", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @Operation(
            summary = "Update ingredient",
            description = "Updates an existing ingredient."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient updated successfully"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found"),
            @ApiResponse(responseCode = "400", description = "Malformed request")
    })
    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponseDto> update(
            @Parameter(description = "Ingredient ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Ingredient updated data", required = true)
            @RequestBody @Valid IngredientUpdateDto dto
    ) {
        return ResponseEntity.ok(ingredientService.update(dto, id));
    }

    @Operation(
            summary = "Delete ingredient",
            description = "Deletes an ingredient by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Ingredient ID", required = true)
            @PathVariable Long id
    ) {
        ingredientService.delete(id);
        return ResponseEntity.ok().build();
    }
}