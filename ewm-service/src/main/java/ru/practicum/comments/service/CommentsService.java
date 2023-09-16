package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.InputCommentDto;

import java.util.List;

public interface CommentsService {

    CommentDto createComment(InputCommentDto inputCommentDto, Long authorId, Long eventId);

    List<CommentDto> getEventComments(Long eventId);

    CommentDto changeComment(InputCommentDto inputCommentDto, Long authorId, Long eventId, Long commentId);

    void removeByCommentIdAndAuthorId(Long commentId, Long authorId);

    void removeCommentById(Long commentId);
}
