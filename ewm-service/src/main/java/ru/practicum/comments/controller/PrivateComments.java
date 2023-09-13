package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.InputCommentDto;
import ru.practicum.comments.service.CommentsService;
import ru.practicum.exception.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@RestController()
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateComments  {

    private final CommentsService commentsService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("{eventId}/comments")
    public CommentDto createComment(@PathVariable Long eventId,
                                    @PathVariable Long userId,
                                    @RequestBody @Valid InputCommentDto inputCommentDto,
                                    HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return commentsService.createComment(inputCommentDto, userId, eventId);
    }

    @PatchMapping("{eventId}/comments/{commentId}")
    public CommentDto patchComment(@PathVariable Long eventId,
                                   @PathVariable Long userId,
                                   @RequestBody InputCommentDto inputCommentDto,
                                   @PathVariable Long commentId,
                                   HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return commentsService.changeComment(inputCommentDto, userId, eventId, commentId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{ignoredEventId}/comments/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId,
                              @PathVariable Long ignoredEventId,
                              HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        commentsService.removeByCommentIdAndAuthorId(commentId, userId);
    }

    private void dateTimeValidate(LocalDateTime localDateTime) {
        if (localDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                    "Вы не можете добавить событие, которое проходит раньше чем за два часа от текущей даты "
                            + localDateTime);
        }
    }
}
