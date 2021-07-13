package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    public static String AUTH_TOKEN = "";
    private final String BASE_URL;
    public AuthenticatedUser currentUser;
    public RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url, AuthenticatedUser currentUser) {
        BASE_URL = url;
        this.currentUser = currentUser;
        AUTH_TOKEN = currentUser.getToken();
    }

    //method to get balance by user ID:
    public Double getBalanceByUserId(int userId) {
        double balance = 0;
        try {
            balance = restTemplate.exchange(BASE_URL + "account/balance/" + userId, HttpMethod.GET, makeAuthEntity(), Double.class).getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }


    //method to update balance--incomplete:
    public void updateBalanceByUserId (Integer userId, Double diff) {

        Account account = new Account();
        account.setUserId(userId);

        //Comment: Do we need a request map in Server that links userId to accountId?
        //Integer accountId = 0;
        //accountId = restTemplate.exchange(BASE_URL + "accounts/" + userId, HttpMethod.GET, makeAuthEntity(), Integer.class).getBody();
        //account.setAccountId(accountId);

        Double balance;
        balance = restTemplate.exchange(BASE_URL + "account/balance/" + userId, HttpMethod.GET, makeAuthEntity(), Double.class).getBody();
        account.setBalance(balance + diff);

        try {
            restTemplate.exchange(BASE_URL + "account/balance/" + userId + "/" + diff, HttpMethod.PUT, makeAccountEntity(account), Account.class);
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
        } catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Creates a new {HttpEntity} with the `Authorization: Bearer:` header and an
     * auction request body
     *
     * @param account
     * @return entity
     */
    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Account> entity = new HttpEntity<>(account, headers);
        return entity;
    }


    /**
     * Returns an {HttpEntity} with the `Authorization: Bearer:` header
     *
     * @return {HttpEntity}
     */
    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

}
