package ru.practicum.category.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Data
public class CategoryOutDto {
    private final Long id;
    private final String name;
}
