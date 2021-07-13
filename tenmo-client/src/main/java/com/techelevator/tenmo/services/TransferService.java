package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {

    public static String AUTH_TOKEN = "";
    private final String BASE_URL;
    public final AuthenticatedUser currentUser;
    public RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url, AuthenticatedUser currentUser) {
        BASE_URL = url;
        this.currentUser = currentUser;
        AUTH_TOKEN = currentUser.getToken();
    }

//method to see details of transfer by transfer id--needs request map in server


// method to retrieve all transfers by user ID. Convert to List.
public Transfer [] getTransfersListByUserId(int userId) {
    try {
        return restTemplate.exchange(BASE_URL + "transfers/" + userId, HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
    } catch (RestClientResponseException ex) {
        System.out.println(ex.getMessage());
        return null;
    } catch (ResourceAccessException e) {
        System.out.println(e.getMessage());
        return null;
    }
}

// method to create new transfer. Do we need something for the "to" account?
public Transfer addTransfer(int toUSerId, double amount) {
        try {
            return restTemplate.exchange(BASE_URL + "send_money/" + currentUser.getUser().getId() + "/" + toUSerId + "/" + amount,
                    HttpMethod.POST, makeAuthEntity(), Transfer.class).getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getMessage());
            return null;
        } catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }
}


    /**
     * Creates a new {HttpEntity} with the `Authorization: Bearer:` header and an
     * auction request body
     *
     * @param transfer
     * @return entity
     */
    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
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
