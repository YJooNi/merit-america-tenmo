package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    List<Account> getAllAccounts();

    Account getAccountByUserId(int id);
    Account getAccountByAccountId(int id);
    Account getAccountByUsername(String username);
    BigDecimal addingBalance(BigDecimal amount, int id);
    BigDecimal subtractingBalance(BigDecimal amount, int id);
}
