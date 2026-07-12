package com.velas.candil.services.ingredientCatalog;

import com.velas.candil.entities.ingredient.IngredientCatalog;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IngredientCatalogService {

    void importExcel(MultipartFile file);
    List<IngredientCatalog> findAllIngredients();
}
