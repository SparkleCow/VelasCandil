package com.velas.candil.entities.candle;

import com.velas.candil.entities.ingredient.Ingredient;
import com.velas.candil.models.candle.CategoryEnum;
import com.velas.candil.models.candle.FeatureEnum;
import com.velas.candil.models.candle.MaterialEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "candles")
@EntityListeners(AuditingEntityListener.class)
public class Candle{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String principalImage;
    private Integer stock;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_materials", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "material")
    @Enumerated(EnumType.STRING)
    private Set<MaterialEnum> materialEnums = new HashSet<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_features", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "feature")
    @Enumerated(EnumType.STRING)
    private Set<FeatureEnum> featureEnums = new HashSet<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Set<CategoryEnum> categories = new HashSet<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> images = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "candle", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Ingredient> ingredients = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal manufacturingCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal profit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    public void removeStock(Integer quantity){
        this.stock -= quantity;
    }

    public void addStock(Integer quantity){
        this.stock += quantity;
    }
}