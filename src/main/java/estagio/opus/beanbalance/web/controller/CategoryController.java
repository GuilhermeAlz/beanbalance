package estagio.opus.beanbalance.web.controller;

import estagio.opus.beanbalance.security.CurrentUserService;
import estagio.opus.beanbalance.service.CategoryService;
import estagio.opus.beanbalance.web.dto.category.CategoryRequest;
import estagio.opus.beanbalance.web.dto.category.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAllAccessibleByUser(currentUserService.getCurrentUser().localUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request, currentUserService.getCurrentUser().localUserId());
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable UUID id,
                                   @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request, currentUserService.getCurrentUser().localUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id, currentUserService.getCurrentUser().localUserId());
    }
}
