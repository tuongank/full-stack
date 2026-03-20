package com.project.ecommerce.entity;


import com.project.ecommerce.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product"})
@EqualsAndHashCode(exclude = {"product"})
public class ProductFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_type")
    private SkinType skinType;

    @ElementCollection(targetClass = SkinConcern.class)
    @CollectionTable(
            name = "product_feature_skin_concerns",
            joinColumns = @JoinColumn(name = "product_feature_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "skin_concern")
    @Builder.Default
    private Set<SkinConcern> skinConcerns = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "product_feature_ingredients",
            joinColumns = @JoinColumn(name = "product_feature_id")
    )
    @Column(name = "ingredient")
    @Builder.Default
    private Set<String> ingredients = new HashSet<>();

    @ElementCollection(targetClass = FreeFrom.class)
    @CollectionTable(
            name = "product_feature_free_from",
            joinColumns = @JoinColumn(name = "product_feature_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "free_from")
    @Builder.Default
    private Set<FreeFrom> freeFrom = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Texture texture;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_time")
    private UsageTime usageTime;

    @Column(name = "ph_level")
    private Double phLevel;

    @Column(name = "pregnancy_safe")
    private Boolean pregnancySafe;
}

