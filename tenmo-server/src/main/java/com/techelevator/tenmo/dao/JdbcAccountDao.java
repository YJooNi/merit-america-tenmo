package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "select account_id, user_id, balance from account;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while(result.next()) {
            accounts.add(mapRowToAccount(result));
        }
        return accounts;
    }

    @Override
    public Account getAccountByUserId(int id) {
        Account account = null;
        String sql = "select account_id, user_id, balance from account where user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if(result.next()) {
            account = mapRowToAccount(result);
        }
        return account;
    }

    @Override
    public Account getAccountByAccountId(int id) {
        Account account = null;
        String sql = "select account_id, user_id, balance from account where account_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if(result.next()) {
            account = mapRowToAccount(result);
        }
        return account;
    }

    @Override
    public Account getAccountByUsername(String username) {
        Account account = null;
        String sql = "select a.account_id, a.user_id, a.balance from account a join tenmo_user tu on tu.user_id = a.user_id where tu.username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        if(result.next()) {
            account = mapRowToAccount(result);
        }
        return account;
    }

    @Override
    public BigDecimal addingBalance(BigDecimal amount,int id) {
        String sql = "UPDATE account SET balance = balance + ? WHERE account_id = ? returning balance;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, amount, id);
        BigDecimal balance = null;
        if (result.next()) {
            balance = result.getBigDecimal("balance");
        }
        return balance;
    }

    @Override
    public BigDecimal subtractingBalance(BigDecimal amount, int id) {
        String sql = "UPDATE account SET balance = balance - ? WHERE account_id = ? returning balance;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, amount, id);
        BigDecimal balance = null;
        if (result.next()) {
            balance = result.getBigDecimal("balance");
        }
        return balance;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
