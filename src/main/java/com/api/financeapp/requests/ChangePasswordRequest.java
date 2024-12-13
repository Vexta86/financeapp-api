package com.api.financeapp.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String code;
    private String emailAddress;
    private String newPassword;
}
