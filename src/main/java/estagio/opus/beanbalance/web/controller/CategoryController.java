package estagio.opus.beanbalance.web.controller;

import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.service.CategoryService;
import estagio.opus.beanbalance.web.dto.category.CategoryRequest;
import estagio.opus.beanbalance.web.dto.category.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> findAll(@AuthenticationPrincipal User user) {
        return categoryService.findAllAccessibleByUser(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request,
                                   @AuthenticationPrincipal User user) {
        return categoryService.create(request, user);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable UUID id,
                                   @Valid @RequestBody CategoryRequest request,
                                   @AuthenticationPrincipal User user) {
        return categoryService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        categoryService.delete(id, user);
    }
}
