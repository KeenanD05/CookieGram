package com.group2.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_name", unique = true, nullable = false)
    private AppRole roleName;
    
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> users = new HashSet<>();

    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
    
    // Helper method to manage bidirectional relationship
    public void addUser(User user) {
        this.users.add(user);
        user.getRoles().add(this);
    }
    
    // Helper method to manage bidirectional relationship
    public void removeUser(User user) {
        this.users.remove(user);
        user.getRoles().remove(this);
    }
}