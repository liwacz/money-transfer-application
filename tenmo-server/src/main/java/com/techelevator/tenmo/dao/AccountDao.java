package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {
    Double findAccountBalance(int userId);
    Integer findAccountByUserId(int userId);
    void updateBalance(int accountId, double difference);
}
