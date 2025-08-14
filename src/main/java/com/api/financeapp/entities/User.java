package com.api.financeapp.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String emailAddress;

    @Column()
    private String password;

    private String name;
    private String lastName;

    private boolean isActive;

    // Default constructor
    public User() {

    }

    // Constructor with parameters
    public User(String emailAddress, String password, String name, String lastName) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
    }

    @Enumerated(EnumType.STRING)
    private Role role;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }


    @Override
    public String getUsername() {
        return emailAddress;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id) &&
                Objects.equals(emailAddress, user.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emailAddress);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", emailAddress='" + emailAddress + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return isActive;
    }
}
