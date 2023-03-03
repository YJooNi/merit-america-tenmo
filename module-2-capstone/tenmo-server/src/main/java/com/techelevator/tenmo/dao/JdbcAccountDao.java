package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "select account_id, user_id, balance from account;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while(result.next()) {
            accounts.add(mapRowToUser(result));
        }
        return accounts;
    }

    @Override
    public Account getAccountById(int id) {
        Account account = null;
        String sql = "select account_id, user_id, balance from account where user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if(result.next()) {
            account = mapRowToUser(result);
        }
        return account;
    }

    @Override
    public int getAccountIdByUsername(String username) {
        int accountId = 0;
        String sql = "select account_id from account join public.tenmo_user tu on tu.user_id = account.user_id where username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        if(result.next()) {
            accountId = result.getInt("account_id");
        }
        return accountId;
    }

    @Override
    public Account findByUsername(String username) {
        Account account = null;
        String sql = "select a.account_id, a.user_id, a.balance from account a join tenmo_user tu on tu.user_id = a.user_id where tu.username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        if(result.next()) {
            account = mapRowToUser(result);
        }
        return account;
    }

    @Override
    public int findIdByUsername(String username) {
        int id = 0;
        String sql = "select a.user_id from account a join tenmo_user tu on tu.user_id = a.user_id where tu.username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        if(result.next()) {
            id = result.getInt("user_id");
        }
        return id;
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

    private Account mapRowToUser(SqlRowSet rs) {
        Account account = new Account();
        account.setUserId(rs.getInt("user_id"));
        account.setAccountId(rs.getInt("account_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }

}
