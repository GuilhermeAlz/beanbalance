package estagio.opus.beanbalance.web.controller;

import estagio.opus.beanbalance.security.CurrentUserService;
import estagio.opus.beanbalance.service.BudgetService;
import estagio.opus.beanbalance.web.dto.budget.BudgetRequest;
import estagio.opus.beanbalance.web.dto.budget.BudgetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<BudgetResponse> findAll(@RequestParam(required = false) String month) {
        String referenceMonth = month != null ? month : YearMonth.now().toString();
        return budgetService.findAllByUserAndMonth(currentUserService.getCurrentUser().localUserId(), referenceMonth);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse create(@Valid @RequestBody BudgetRequest request) {
        return budgetService.create(request, currentUserService.getCurrentUser().localUserId());
    }

    @PutMapping("/{id}")
    public BudgetResponse update(@PathVariable UUID id,
                                 @Valid @RequestBody BudgetRequest request) {
        return budgetService.update(id, request, currentUserService.getCurrentUser().localUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        budgetService.delete(id, currentUserService.getCurrentUser().localUserId());
    }
}
