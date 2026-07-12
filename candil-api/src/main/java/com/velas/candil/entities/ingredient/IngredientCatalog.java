package com.velas.candil.entities.ingredient;

import com.velas.candil.models.ingredient.IngredientType;
import com.velas.candil.models.ingredient.IngredientUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredient_catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String ingredientCode;

    @Column(nullable = false)
    private String ingredientName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngredientType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngredientUnit ingredientUnit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    private String supplier;

    private String supplierContact;

    private Boolean active = true;
}