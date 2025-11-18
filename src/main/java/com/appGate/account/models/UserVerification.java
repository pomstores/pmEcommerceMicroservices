package com.appGate.account.models;

import com.appGate.account.enums.VerificationStatus;
import com.appGate.account.enums.VerificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_type", nullable = false)
    private VerificationType verificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VerificationStatus status = VerificationStatus.PENDING;

    @Column(name = "verification_value")
    private String verificationValue; // BVN number, NIN number, etc.

    @Column(name = "verification_reference")
    private String verificationReference; // Reference from third-party API

    @Column(name = "verification_response", length = 5000)
    private String verificationResponse; // Full API response (JSON)

    @Column(name = "verified_name")
    private String verifiedName; // Name from verification provider

    @Column(name = "verified_dob")
    private String verifiedDateOfBirth;

    @Column(name = "verified_phone")
    private String verifiedPhone;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "retry_count")
    private Integer retryCount = 0;
}
