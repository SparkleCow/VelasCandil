package com.velas.candil.controllers;

import com.velas.candil.entities.ingredient.IngredientCatalog;
import com.velas.candil.services.ingredientCatalog.IngredientCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/catalog")
public class IngredientCatalogController {

    private final IngredientCatalogService ingredientCatalogService;

    @PostMapping
    public ResponseEntity<Void> importExcel(@RequestParam MultipartFile file) {
        ingredientCatalogService.importExcel(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<IngredientCatalog>> getAllIngredients(){
        return ResponseEntity.ok(ingredientCatalogService.findAllIngredients());
    }
}
