package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UserDto {
    private Long id;
    @NotNull
    private String name;
    @Email
    private String email;
}
