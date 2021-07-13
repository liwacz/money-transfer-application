package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class UserService {

    public static String AUTH_TOKEN = "";
    private final String BASE_URL;
    public final AuthenticatedUser currentUser;
    public RestTemplate restTemplate = new RestTemplate();

    public UserService(String url, AuthenticatedUser currentUser) {
        BASE_URL = url;
        this.currentUser = currentUser;
        AUTH_TOKEN = currentUser.getToken();
    }

    //need to map the following in Server:
    public List<User> getAllUsers() {
        User[] users = null;
        users = restTemplate.exchange(BASE_URL + "users/", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
        return Arrays.asList(users);
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
