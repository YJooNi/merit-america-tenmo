package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

    private int accountId;
    private int userId;
    private BigDecimal balance;

    public Account(int accountId, int userId, BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public Account() { }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void transfer(Account account, BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        account.setBalance(account.getBalance().add(amount));
    }

    @Override
    public String toString() {
        return "Account{" +
                "account_id=" + accountId +
                ", user_id=" + userId +
                ", balance=" + balance +
                '}';
    }
}
