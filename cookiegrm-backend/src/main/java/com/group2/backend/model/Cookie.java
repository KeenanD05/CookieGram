package com.group2.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;



import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cookies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cookie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotBlank
    private String color;

    @NotBlank
    private String message;

    @NotBlank
    private String icing;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Positive
    private Double basePrice;

    @PositiveOrZero
    private Double discount;

    private boolean available = true;
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "cookie_ingredients", joinColumns = @JoinColumn(name = "cookie_id"))
    @Column(name = "ingredient")
    private Set<String> ingredients = new HashSet<>();


    private boolean customizable = true;


}