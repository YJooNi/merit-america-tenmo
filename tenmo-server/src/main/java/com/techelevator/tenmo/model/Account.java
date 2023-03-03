package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

    private int userId;
    private BigDecimal balance;
    private int accountId;

    public Account(){}

    public Account(int userId, BigDecimal balance, int accountId) {
        this.userId = userId;
        this.balance = balance;
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
