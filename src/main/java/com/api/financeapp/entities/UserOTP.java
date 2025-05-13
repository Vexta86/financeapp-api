package com.api.financeapp.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Entity
public class UserOTP {
    @EmbeddedId
    private UserOTPId id;

    private String otp;

    private Instant otpTimestamp;

    public Instant getOtpTimestamp() {
        return otpTimestamp;
    }

    public String getOtp() {
        return otp;
    }

    public UserOTPId getId() {
        return id;
    }

    public void setId(UserOTPId id) {
        this.id = id;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setOtpTimestamp(Instant otpTimestamp) {
        this.otpTimestamp = otpTimestamp;
    }
}
