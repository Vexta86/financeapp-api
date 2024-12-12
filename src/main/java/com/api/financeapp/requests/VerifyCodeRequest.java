package com.api.financeapp.requests;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeRequest {
    private String code;
    private String emailAddress;
}
