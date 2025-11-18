package com.appGate.rbac.models;

import com.appGate.rbac.enums.RoleEnum;
import com.appGate.rbac.enums.GenderEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password", "resetOtp" })
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phoneNumber", unique = true)
    private String phoneNumber;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "password")
    private String password;

    @Column(name = "resetOtp")
    private String resetOtp;
    
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
