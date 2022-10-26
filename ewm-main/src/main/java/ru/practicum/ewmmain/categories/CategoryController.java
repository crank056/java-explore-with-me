package ru.practicum.ewmmain.categories;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.categories.model.CategoryDto;
import ru.practicum.ewmmain.exceptions.NotFoundException;
import ru.practicum.ewmmain.exceptions.ValidationException;

import java.util.List;

@RestController
@RequestMapping
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/categories")
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/admin/categories")
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public boolean deleteCategory(@PathVariable Long catId) {
        return categoryService.deleteCategory(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllFromPage(@RequestParam(required = false, defaultValue = "0") int from,
                                            @RequestParam(required = false, defaultValue = "10") int size)
        throws ValidationException {
        return categoryService.getAllFromPage(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getFromId(@PathVariable Long catId) throws NotFoundException {
        return categoryService.getFromId(catId);
    }
}
