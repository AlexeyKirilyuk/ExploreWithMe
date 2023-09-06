package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
//import org.hibernate.validator.constraints.Length;
//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank
    @Length(min = 2, max = 256)
    private String name;
    @NotBlank
    @Length(min = 6, max = 256)
    @Email
    private String email;
}
