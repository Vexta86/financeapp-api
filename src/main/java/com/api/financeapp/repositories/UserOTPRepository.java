package com.api.financeapp.repositories;

import com.api.financeapp.entities.UserOTP;
import com.api.financeapp.entities.UserOTPId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOTPRepository extends JpaRepository<UserOTP, UserOTPId > {
}
