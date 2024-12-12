package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String name;
    private String lastName;
    private String emailAddress;
    private Double netWorth;
    private String role;

}
