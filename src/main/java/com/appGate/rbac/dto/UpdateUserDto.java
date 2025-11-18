package com.appGate.rbac.dto;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;    
}
