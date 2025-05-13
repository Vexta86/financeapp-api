package com.api.financeapp.requests;

import lombok.Getter;
import lombok.Setter;


public class ChangePasswordRequest {
    private String code;
    private String emailAddress;
    private String newPassword;

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getCode() {
        return code;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
