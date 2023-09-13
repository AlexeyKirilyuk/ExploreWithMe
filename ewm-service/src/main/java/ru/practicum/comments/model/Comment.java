package ru.practicum.comments.model;

import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;
import java.time.LocalDateTime;

public class Comment {

    private Long id;
    private String text;
    private Event event;
    private User author;
    private LocalDateTime created;
}