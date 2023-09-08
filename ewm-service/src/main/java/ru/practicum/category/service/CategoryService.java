package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryOutDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryOutDto saveCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long categoryId);

    CategoryOutDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto);

    List<CategoryOutDto> getCategories(int from, int size);

    CategoryOutDto getCategoryById(Long id);

}
