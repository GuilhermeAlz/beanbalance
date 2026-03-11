package estagio.opus.beanbalance.web.controller;

import estagio.opus.beanbalance.domain.entity.User;
import estagio.opus.beanbalance.service.AccountService;
import estagio.opus.beanbalance.web.dto.account.AccountRequest;
import estagio.opus.beanbalance.web.dto.account.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountResponse> findAll(@AuthenticationPrincipal User user) {
        return accountService.findAllByUser(user);
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return accountService.findByIdAndUser(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountRequest request,
                                  @AuthenticationPrincipal User user) {
        return accountService.create(request, user);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody AccountRequest request,
                                  @AuthenticationPrincipal User user) {
        return accountService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        accountService.delete(id, user);
    }
}
