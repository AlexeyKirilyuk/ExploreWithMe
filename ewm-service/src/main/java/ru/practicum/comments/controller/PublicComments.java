package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicComments  {

    private final CommentsService commentsService;

    @GetMapping("/{id}/comments")
    public List<CommentDto> getComments(@PathVariable Long id,
                                        HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return commentsService.getEventComments(id);
    }
}