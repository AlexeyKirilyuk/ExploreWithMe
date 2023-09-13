package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto commentToCommentDto(Comment comment);

    @Mapping(target = "authorName", source = "comment.author.name")
    List<CommentDto> commentToCommentDto(List<Comment> comments);
}
