package estagio.opus.beanbalance.web.mapper;

import estagio.opus.beanbalance.domain.entity.Budget;
import estagio.opus.beanbalance.web.dto.budget.BudgetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "spentAmount", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    BudgetResponse toResponse(Budget budget);

    default BudgetResponse toResponseWithSpent(Budget budget, BigDecimal spentAmount) {
        BigDecimal remaining = budget.getLimitAmount().subtract(spentAmount);
        return new BudgetResponse(
                budget.getId(),
                budget.getLimitAmount(),
                spentAmount,
                remaining,
                budget.getReferenceMonth(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getCreatedAt()
        );
    }
}
