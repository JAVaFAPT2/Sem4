package com.example.beskbd.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_users")
public class User extends BaseEntity implements UserDetails {
    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password; // Note: In real scenarios, exclude from JSON serialization with @JsonIgnore
    private Role authority;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String resetToken;
    private LocalDateTime resetTokenExpiryDate;
    private boolean accountNonExpired;
    private boolean isEnabled;
    private boolean accountNonLocked;
    private boolean isDeleted = false;
    private boolean credentialsNonExpired;
    private String imageUrl;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany
    private List<Review> reviews;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(authority);
    }
}
