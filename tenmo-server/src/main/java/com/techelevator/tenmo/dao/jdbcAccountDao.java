package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class jdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public jdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Double findAccountBalance(int userId) {
        String sql = "SELECT balance FROM accounts WHERE user_id= ?;";
        Double accountBalance = jdbcTemplate.queryForObject(sql, Double.class, userId);
        if (accountBalance != null) {
            return accountBalance;
        } else {
            return -1.0;
        }
    }

    @Override
    public Integer findAccountByUserId(int userId) {
        String sql = "SELECT account_id FROM accounts WHERE user_id= ?;";
        Integer account_id = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return account_id;
    }

    @Override
    public void updateBalance(int accountId, double difference) {
        String sql = "Update accounts set balance = balance + ? where account_id = ?;";
        jdbcTemplate.update(sql, difference, accountId);
    }
}
