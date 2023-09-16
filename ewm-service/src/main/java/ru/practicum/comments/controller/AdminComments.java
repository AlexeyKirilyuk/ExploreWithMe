package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.service.CommentsService;
import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminComments {

    private final CommentsService commentsService;


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{ignoredEventId}/comments/{commentId}")
    public void removeCommentByAdmin(@PathVariable Long ignoredEventId, @PathVariable Long commentId,
                                     HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        commentsService.removeCommentById(commentId);
    }
}
