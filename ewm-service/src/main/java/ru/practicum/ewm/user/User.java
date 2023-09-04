package ru.practicum.ewm.user;

import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class User {
    private Long id;
    private String name;
    private String email;
}