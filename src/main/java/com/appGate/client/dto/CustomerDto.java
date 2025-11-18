package com.appGate.client.dto;

import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

import com.appGate.client.enums.GenderEnum;
import lombok.Data;

@Data
public class CustomerDto {

    private String accountNumber;
    private String email;
    private String surname ;
    private String firstName;
    private String dob;
    private String phoneNumber;
    private String occupation;
    private String nationality;
    private String nin;
    private String bvn;
    private String contactAddress;
    private String officeAddress;
    private String nextOfKin;
    private String nextOfKinAddress;
    @Nullable
    private MultipartFile passport;
    @Nullable
    private MultipartFile signature;
    private GenderEnum gender;
}
