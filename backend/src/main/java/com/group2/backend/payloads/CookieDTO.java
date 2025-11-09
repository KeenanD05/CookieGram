package com.group2.backend.payloads;

import lombok.Data;
import java.util.Set;

@Data
public class CookieDTO {
    private Long id;
    private String name;
    private String type;
    private String color;
    private String message;
    private String icing;
    private String description;
    private Double basePrice;
    private Double discount;
    private boolean available;
    private String imageUrl;
    private Set<String> ingredients;
    private boolean customizable;
}