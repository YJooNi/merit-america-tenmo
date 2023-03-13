package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/accounts")
public class AccountController {

    private final UserDao userDao;
    private final AccountDao accountDao;

    public AccountController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @GetMapping("/myBalance")
    public BigDecimal getBalance(Principal principal) {
        Account account = accountDao.getAccountByUsername(principal.getName());
        BigDecimal balance =  account.getBalance();
        return balance;
    }

    @GetMapping("/myAccount")
    public Account getAccount(Principal principal) {
        Account account = accountDao.getAccountByUsername(principal.getName());
        return account;
    }
    @GetMapping()
    public List<Account> getListOfAccounts(){
        return accountDao.getAllAccounts();
    }

    @GetMapping("/user/{id}")
    public Account getAccountByUserId (@PathVariable int id) {
        return accountDao.getAccountByUserId(id);
    }

    @GetMapping("/account/{id}")
    public Account getAccountByAccountId (@PathVariable int id) {
        return accountDao.getAccountByAccountId(id);
    }
}
