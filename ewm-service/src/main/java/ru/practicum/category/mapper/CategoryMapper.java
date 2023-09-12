package ru.practicum.category.mapper;

import ru.practicum.category.dto.CategoryOutDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

    CategoryOutDto categoryToCategoryOutDto(Category category);

    List<CategoryOutDto> categoriesToCategoriesOutDto(List<Category> paginatedCategories);
}
