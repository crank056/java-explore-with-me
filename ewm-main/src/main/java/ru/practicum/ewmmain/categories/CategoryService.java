package ru.practicum.ewmmain.categories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.categories.model.Category;
import ru.practicum.ewmmain.categories.model.CategoryDto;
import ru.practicum.ewmmain.categories.model.CategoryMapper;
import ru.practicum.ewmmain.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.toEntity(categoryDto)));
    }

    public CategoryDto updateCategory(CategoryDto categoryDto) {
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.toEntity(categoryDto)));
    }

    public boolean deleteCategory(Long catId) {
        categoryRepository.deleteById(catId);
        return !categoryRepository.existsById(catId);
    }

    public List<CategoryDto> getAllFromPage(int from, int size) throws ValidationException {
        validatePageSize(from, size);
        Pageable page = PageRequest.of(from / size, size, Sort.by("id").descending());
        List<Category> categories = categoryRepository.findAll(page).getContent();
        List<CategoryDto> categoryDto = new ArrayList<>();
        for(Category category: categories) {
            categoryDto.add(CategoryMapper.toDto(category));
        }
        return categoryDto;
    }

    public CategoryDto getFromId(Long catId) {
        return CategoryMapper.toDto(categoryRepository.getReferenceById(catId));
    }

    private void validatePageSize(int from, int size) throws ValidationException {
        if (from < 0 || size < 1) throw new ValidationException("Неверные значения формата");
    }
}
