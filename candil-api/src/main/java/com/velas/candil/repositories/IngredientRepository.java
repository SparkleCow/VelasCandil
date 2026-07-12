package com.velas.candil.repositories;

import com.velas.candil.entities.ingredient.Ingredient;
import com.velas.candil.models.ingredient.IngredientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}
