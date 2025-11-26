package com.eadcw.FlowerGiftApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
}
