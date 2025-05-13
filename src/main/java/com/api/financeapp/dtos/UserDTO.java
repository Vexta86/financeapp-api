package com.api.financeapp.dtos;

import com.api.financeapp.entities.User;

public record UserDTO(
        String name,
        String lastName,
        String emailAddress,
        Double netWorth,
        String role
) {


}
