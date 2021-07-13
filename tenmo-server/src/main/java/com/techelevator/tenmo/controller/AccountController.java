package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {
    private AccountDao accountDao;

    public AccountController(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "account/balance/{userid}", method = RequestMethod.GET)
    public Double getBalanceByUserId(@PathVariable int userid) {
        return accountDao.findAccountBalance(userid);
    }

    @RequestMapping(path = "account/balance/{id}/{diff}", method = RequestMethod.PUT)
    public void updateBalanceByUserId(@RequestBody @PathVariable int id, @PathVariable double diff) {
        accountDao.updateBalance(id, diff);
    }

}
