package com.api.financeapp.requests;

import lombok.Getter;
import lombok.Setter;


public class GetCodeRequest {
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
