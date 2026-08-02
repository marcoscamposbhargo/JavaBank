package com.javabank.javabank.service;

import com.javabank.javabank.model.Account;
import com.javabank.javabank.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public Account deposit(String accountNumber, Double amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada!"));

        if (amount <= 0) {
            throw new RuntimeException("O valor do depósito deve ser maior que zero.");
        }

        account.setBalance(account.getBalance() + amount);
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(String accountNumber, Double amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada!"));

        if (amount <= 0 || account.getBalance() < amount) {
            throw new RuntimeException("Saldo insuficiente ou valor inválido.");
        }

        account.setBalance(account.getBalance() - amount);
        return accountRepository.save(account);
    }
}
