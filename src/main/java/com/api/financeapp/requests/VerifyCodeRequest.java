package com.api.financeapp.requests;


import lombok.Getter;
import lombok.Setter;


public class VerifyCodeRequest {
    private String code;
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getCode() {
        return code;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
