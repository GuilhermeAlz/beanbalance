package estagio.opus.beanbalance.web.controller;

import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.service.BudgetService;
import estagio.opus.beanbalance.web.dto.budget.BudgetRequest;
import estagio.opus.beanbalance.web.dto.budget.BudgetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public List<BudgetResponse> findAll(@AuthenticationPrincipal User user,
                                        @RequestParam(required = false) String month) {
        String referenceMonth = month != null ? month : java.time.YearMonth.now().toString();
        return budgetService.findAllByUserAndMonth(user, referenceMonth);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse create(@Valid @RequestBody BudgetRequest request,
                                 @AuthenticationPrincipal User user) {
        return budgetService.create(request, user);
    }

    @PutMapping("/{id}")
    public BudgetResponse update(@PathVariable UUID id,
                                 @Valid @RequestBody BudgetRequest request,
                                 @AuthenticationPrincipal User user) {
        return budgetService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        budgetService.delete(id, user);
    }
}
