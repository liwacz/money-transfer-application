package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class jdbcTransferDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;

    public jdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> findAll(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT t.*, tt.transfer_type_desc AS type_desc, ts.transfer_status_desc AS status_desc, " +
                "u1.username AS from_user, u2.username AS to_user FROM transfers t JOIN " +
                "transfer_types tt ON tt.transfer_type_id = t.transfer_type_id JOIN " +
                "transfer_statuses ts ON ts.transfer_status_id = t.transfer_status_id JOIN " +
                "accounts a1 ON a1.account_id = t.account_from JOIN accounts a2 ON a2.account_id = t.account_to JOIN " +
                "users u1 ON u1.user_id = a1.user_id JOIN users u2 ON u2.user_id = a2.user_id " +
                "WHERE u1.user_id = ? OR u2.user_id =?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }
    @Override
    public void addTransfer(Transfer transfer){
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)VALUES(2,2,?,?,?);";
        jdbcTemplate.update(sql, transfer.getAccountFrom(), transfer.getAccountTo(),transfer.getAmount());
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getDouble("amount"));
        transfer.setTransferStatus(rs.getString("status_desc"));
        transfer.setTransferType(rs.getString("type_desc"));
        transfer.setFromUsername(rs.getString("from_user"));
        transfer.setToUsername(rs.getString("to_user"));
        return transfer;
    }
}
