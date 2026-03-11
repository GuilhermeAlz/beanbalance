package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Category;
import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.repository.CategoryRepository;
import estagio.opus.beanbalance.exception.ResourceNotFoundException;
import estagio.opus.beanbalance.web.dto.category.CategoryRequest;
import estagio.opus.beanbalance.web.dto.category.CategoryResponse;
import estagio.opus.beanbalance.web.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllAccessibleByUser(User user) {
        return categoryRepository.findAllAccessibleByUser(user.getId()).stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request, User user) {
        Category category = categoryMapper.toEntity(request);
        category.setUser(user);
        category.setCustom(true);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(UUID categoryId, CategoryRequest request, User user) {
        Category category = getOwnedCategoryOrThrow(categoryId, user.getId());
        categoryMapper.updateEntity(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID categoryId, User user) {
        Category category = getOwnedCategoryOrThrow(categoryId, user.getId());
        categoryRepository.delete(category);
    }

    private Category getOwnedCategoryOrThrow(UUID categoryId, UUID userId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
    }
}
