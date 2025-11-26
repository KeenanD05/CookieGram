package com.group2.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    private String buildingName;

    @NotBlank
    private String street;

    @NotBlank
    private String city;
    @NotBlank
    private String state;
    @NotBlank
    private String zipCode;
    @NotBlank
    private String country;

   // @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    //@JoinTable(name = "user_addresses", joinColumns = @JoinColumn(name = "address_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
   // private User user;
}
