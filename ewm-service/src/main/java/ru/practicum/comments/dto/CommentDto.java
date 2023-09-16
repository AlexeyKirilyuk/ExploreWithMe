package ru.practicum.comments.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private final Long id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}
