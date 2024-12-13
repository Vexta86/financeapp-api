package com.api.financeapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserOTPId implements Serializable {
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
    @Column(nullable = false)
    private OTPType otpType;

    public UserOTPId(User user, OTPType otpType) {
        this.user = user;
        this.otpType = otpType;
    }

    public UserOTPId() {

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserOTPId that = (UserOTPId) obj;
        return Objects.equals(user, that.user) &&
                Objects.equals(otpType, that.otpType);
    }
}
