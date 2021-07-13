package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class TransferController {
    private TransferDao transferDao;
    private AccountDao accountDao;

    public TransferController(TransferDao transferDao, AccountDao accountDao){
        this.transferDao = transferDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "transfers/{userId}", method = RequestMethod.GET)
    public List<Transfer> getTransfersListByUserId(@PathVariable int userId) {
        return transferDao.findAll(userId);
    }


    @RequestMapping(path = "send_money/{userIdFrom}/{userIdTo}/{amount}", method = RequestMethod.POST)
    public void addTransfer(@PathVariable int userIdFrom, @PathVariable int userIdTo, @PathVariable double amount) {

        int accountFromId =  accountDao.findAccountByUserId(userIdFrom);
        int accountToId =  accountDao.findAccountByUserId(userIdTo);
        Transfer newTransfer = new Transfer();
        newTransfer.setAccountFrom(accountFromId);
        newTransfer.setAccountTo(accountToId);
        newTransfer.setAmount(amount);
        accountDao.updateBalance(accountFromId, -1*amount);
        accountDao.updateBalance(accountToId, amount);
        transferDao.addTransfer(newTransfer);
    }

}
