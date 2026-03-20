package com.project.ecommerce.entity;

import com.project.ecommerce.enums.FreeFrom;
import com.project.ecommerce.enums.SkinConcern;
import com.project.ecommerce.enums.SkinType;
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
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_type")
    private SkinType skinType;

    @ElementCollection(targetClass = SkinConcern.class)
    @CollectionTable(
            name = "user_profile_skin_concerns",
            joinColumns = @JoinColumn(name = "user_profile_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "skin_concern")
    @Builder.Default
    private Set<SkinConcern> skinConcerns = new HashSet<>();

    @ElementCollection(targetClass = FreeFrom.class)
    @CollectionTable(
            name = "user_profile_avoid_ingredients",
            joinColumns = @JoinColumn(name = "user_profile_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "avoid_ingredient")
    @Builder.Default
    private Set<FreeFrom> avoidIngredients = new HashSet<>();

    private Integer age;
}
