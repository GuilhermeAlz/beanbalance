package estagio.opus.beanbalance.service;

import estagio.opus.beanbalance.domain.entity.Category;
import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.domain.repository.CategoryRepository;
import estagio.opus.beanbalance.domain.repository.UserRepository;
import estagio.opus.beanbalance.exception.ResourceNotFoundException;
import estagio.opus.beanbalance.web.dto.category.CategoryRequest;
import estagio.opus.beanbalance.web.dto.category.CategoryResponse;
import estagio.opus.beanbalance.web.mapper.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private UUID userId;
    private User user;
    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder().id(userId).build();
        category = Category.builder()
                .id(UUID.randomUUID())
                .name("Custom Category")
                .custom(true)
                .user(user)
                .build();
        categoryResponse = new CategoryResponse(
                category.getId(), "Custom Category", null, true, LocalDateTime.now());
    }

    @Test
    void findAllAccessibleByUser_shouldReturnCategories() {
        when(categoryRepository.findAllAccessibleByUser(userId)).thenReturn(List.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        List<CategoryResponse> result = categoryService.findAllAccessibleByUser(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Custom Category");
    }

    @Test
    void create_shouldSaveAndReturnCategory() {
        var request = new CategoryRequest("New Category", "Description");
        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        CategoryResponse result = categoryService.create(request, userId);

        assertThat(result).isNotNull();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void update_shouldThrowWhenCategoryNotFound() {
        UUID categoryId = UUID.randomUUID();
        var request = new CategoryRequest("Updated", null);
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(categoryId, request, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveCategory() {
        when(categoryRepository.findByIdAndUserId(category.getId(), userId))
                .thenReturn(Optional.of(category));

        categoryService.delete(category.getId(), userId);

        verify(categoryRepository).delete(category);
    }
}
