package ru.practicum.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputCommentDto {

    @NotNull(message = "комментарий не может быть пустым.")
    @NotBlank(message = "комментарий не может быть пустым.")
    private  String text;
}
