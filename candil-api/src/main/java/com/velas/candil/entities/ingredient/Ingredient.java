package com.velas.candil.entities.ingredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.velas.candil.entities.candle.Candle;
import com.velas.candil.models.ingredient.IngredientType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "ingredients")
/*TODO - auditing*/
/*TODO Fix pricing*/
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_catalog_id")
    private IngredientCatalog ingredientCatalog;
    private BigDecimal amount;
    private BigDecimal pricePerUnit;
    private BigDecimal price;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "candle_id")
    private Candle candle;
    @Enumerated(EnumType.STRING)
    private IngredientType ingredientType;

    public Ingredient(IngredientCatalog ingredientCatalog, BigDecimal amount){
        this.amount = amount;
        this.ingredientType = ingredientCatalog.getType();
        this.pricePerUnit = ingredientCatalog.getPricePerUnit();
        this.price = calculatePrice();
    }

    public BigDecimal calculatePrice(){
        return pricePerUnit.multiply(amount);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", amount=" + amount +
                ", pricePerUnit=" + pricePerUnit +
                ", price=" + price +
                '}';
    }
}