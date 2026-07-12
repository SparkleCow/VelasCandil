package com.velas.candil.repositories;

import com.velas.candil.entities.ingredient.IngredientCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientCatalogRepository extends JpaRepository<IngredientCatalog, Long> {

    Optional<IngredientCatalog> findByIngredientCode(String ingredientCode);
}
