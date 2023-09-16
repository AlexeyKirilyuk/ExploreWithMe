package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.InputCommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.event.enumerated.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentsServiceImpl implements CommentsService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;



    @Transactional
    @Override
    public CommentDto createComment(InputCommentDto inputCommentDto, Long authorId, Long eventId) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id= " + authorId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %s не найдено", eventId)));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("Событие с id= %s не найдено", eventId));
        }
        Comment newComment = new Comment(null, inputCommentDto.getText(), event, user, LocalDateTime.now());
        newComment = commentRepository.save(newComment);
        log.info(String.format("Пользователь с id= %s добавил новый комментарий= %s", authorId, newComment));
        return commentMapper.commentToCommentDto(newComment);
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId) {
        List<Comment> eventComments = commentRepository.findAllByEventId(eventId);
        log.info("Получены комментарии ивента с id= " + eventId);
        return commentMapper.commentToCommentDto(eventComments);
    }

    @Transactional
    @Override
    public CommentDto changeComment(InputCommentDto inputCommentDto, Long authorId, Long eventId, Long commentId) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Пользователь с id= " + authorId + " не найден");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %s не найдено", eventId)));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("Событие с id= %s не найдено", eventId));
        }
        Comment commentToChange = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с id= %s не найден", commentId)));
        if (!commentToChange.getAuthor().getId().equals(authorId)) {
            throw new BadRequestException("Вы не можете изменить чужой комментарий");
        }
        if (LocalDateTime.now().isAfter(commentToChange.getCreated().plusDays(1))) {
            throw new BadRequestException("Вы не можете изменять комментарии, которые оставлены более 24 часов назад");
        }
        commentToChange.setText(inputCommentDto.getText());
        commentToChange = commentRepository.save(commentToChange);
        log.info(String.format("Комментарий с id= %s изменён", commentId));
        return commentMapper.commentToCommentDto(commentToChange);
    }

    @Override
    public void removeByCommentIdAndAuthorId(Long commentId, Long authorId) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Пользователь с id= " + authorId + " не найден");
        }
        Comment commentToChange = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с id= %s не найден", commentId)));
        if (!commentToChange.getAuthor().getId().equals(authorId)) {
            throw new BadRequestException("Вы не можете удалить чужой комментарий");
        }
        commentRepository.deleteById(commentId);
        log.info(String.format("Комментарий с id= %s удалён", commentId));
    }

    @Override
    public void removeCommentById(Long commentId) {
        commentRepository.deleteById(commentId);
        log.info(String.format("Комментарий с id= %s удалён", commentId));
    }
}
