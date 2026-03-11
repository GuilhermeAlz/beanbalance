package estagio.opus.beanbalance.web.mapper;

import estagio.opus.beanbalance.domain.entity.Category;
import estagio.opus.beanbalance.web.dto.category.CategoryRequest;
import estagio.opus.beanbalance.web.dto.category.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryRequest request);

    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
