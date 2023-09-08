package ru.practicum.event.mapper;

import java.util.List;

import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto commentToCommentDto(Comment comment);

    @Mapping(target = "authorName", source = "comment.author.name")
    List<CommentDto> commentToCommentDto(List<Comment> comments);
}
