package com.appGate.delivery.service;

import com.appGate.delivery.dto.RiderChangePasswordDto;
import com.appGate.delivery.dto.RiderForgotPasswordDto;
import com.appGate.delivery.dto.RiderLoginDto;
import com.appGate.delivery.models.Rider;
import com.appGate.delivery.repository.RiderRepository;
import com.appGate.delivery.response.BaseResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RiderAuthService {

    private final RiderRepository riderRepository;
    private final PasswordEncoder passwordEncoder;
    private final String jwtSecret;
    private final int jwtExpirationMs;

    public RiderAuthService(
            RiderRepository riderRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.jwtSecret}") String jwtSecret,
            @Value("${app.jwtExpirationMs}") int jwtExpirationMs) {
        this.riderRepository = riderRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public BaseResponse login(RiderLoginDto loginDto) {
        Rider rider = riderRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (rider.getSuspended()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been suspended: " + rider.getReasonForSuspension());
        }

        if (rider.getPassword() == null || !passwordEncoder.matches(loginDto.getPassword(), rider.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = generateRiderToken(rider);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("riderId", rider.getRiderId());
        response.put("email", rider.getEmail());
        response.put("fullName", rider.getSurName() + " " + rider.getOtherName());

        return new BaseResponse(HttpStatus.OK.value(), "Login successful", response);
    }

    public BaseResponse changePassword(Long riderId, RiderChangePasswordDto dto) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rider not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), rider.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        rider.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        riderRepository.save(rider);

        return new BaseResponse(HttpStatus.OK.value(), "Password changed successfully", null);
    }

    public BaseResponse forgotPassword(RiderForgotPasswordDto dto) {
        Rider rider = riderRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No rider found with this email"));

        // TODO: Implement password reset email sending
        // For now, just return success

        return new BaseResponse(HttpStatus.OK.value(),
                "Password reset instructions have been sent to your email", null);
    }

    private String generateRiderToken(Rider rider) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_16));

        return Jwts.builder()
                .setSubject(rider.getEmail())
                .setIssuedAt(new Date())
                .claim("riderId", rider.getRiderId())
                .claim("email", rider.getEmail())
                .claim("fullName", rider.getSurName() + " " + rider.getOtherName())
                .claim("type", "RIDER")
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }
}
