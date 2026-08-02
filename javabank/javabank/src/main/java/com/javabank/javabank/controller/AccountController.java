package com.javabank.javabank.controller;

import com.javabank.javabank.model.Account;
import com.javabank.javabank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account newAccount = accountService.createAccount(account);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable String accountNumber,
            @RequestParam Double amount) {
        Account updatedAccount = accountService.deposit(accountNumber, amount);
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable String accountNumber,
            @RequestParam Double amount) {
        Account updatedAccount = accountService.withdraw(accountNumber, amount);
        return ResponseEntity.ok(updatedAccount);
    }
}
