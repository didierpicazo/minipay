package com.didier.minipay.service;

import com.didier.minipay.dto.AccountResponse;
import com.didier.minipay.dto.CreateAccountRequest;
import com.didier.minipay.exception.AccountNotFoundException;
import com.didier.minipay.model.Account;
import com.didier.minipay.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        Account account = new Account();
        account.setOwnerName(request.ownerName());
        account.setBalance(request.initialBalance() != null
                ? request.initialBalance()
                : BigDecimal.ZERO);

        Account saved = accountRepository.save(account);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return toResponse(account);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getOwnerName(),
                account.getBalance());
    }
}