package com.project.ecommerce.entity;

import com.project.ecommerce.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private  String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, name = "phone_number")
    private String phoneNumber;

    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItemList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> address;

    @Column(name = "created_date")
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    @Builder.Default
    private LocalDateTime updatedDate = LocalDateTime.now();
}
