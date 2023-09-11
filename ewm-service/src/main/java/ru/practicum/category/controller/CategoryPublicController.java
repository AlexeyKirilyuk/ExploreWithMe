package ru.practicum.category.controller;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.category.dto.CategoryOutDto;
import ru.practicum.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RequestMapping("/categories")
@RequiredArgsConstructor
@RestController
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping()
    public List<CategoryOutDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size,
                                              HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return categoryService.getCategories(from,size);
    }

    @GetMapping("/{id}")
    public CategoryOutDto getCategoryById(@PathVariable Long id,
                                          HttpServletRequest httpServletRequest) {
        log.info("*************************************************************************************************************************");
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                                                  httpServletRequest.getRequestURI(),
                                                  httpServletRequest.getRemoteAddr());
        return categoryService.getCategoryById(id);
    }
}
