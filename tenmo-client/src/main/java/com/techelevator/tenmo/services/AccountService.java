package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountService {

    private final String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        this.baseUrl = url;
    }


    public List<Account> getListOfUsers(AuthenticatedUser currentUser) {
        List<Account> listOfUsers = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Account[]> entity = new HttpEntity<>(headers);
        try {
            HttpEntity<Account[]> response = restTemplate.exchange(baseUrl + "accounts", HttpMethod.GET, entity, Account[].class);
            listOfUsers = Arrays.asList(response.getBody());
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return listOfUsers;
    }

    public BigDecimal viewBalance (AuthenticatedUser currentUser) {
        BigDecimal balance = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        try {
            HttpEntity<BigDecimal> response = restTemplate.exchange(baseUrl + "accounts/myBalance", HttpMethod.GET, entity, BigDecimal.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public int getAccountById(int id, AuthenticatedUser currentUser){
        int accountId = 0;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        try {
            HttpEntity<Account> response = restTemplate.exchange(baseUrl + "accounts/" + id, HttpMethod.GET, entity, Account.class);
            accountId = response.getBody().getAccountId();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return accountId;
    }

    public String getUsernameByUserId(int id, AuthenticatedUser currentUser) {
        String username = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        try {
            HttpEntity<User> response = restTemplate.exchange(baseUrl + "users/" + id, HttpMethod.GET, entity, User.class);
            username = response.getBody().getUsername();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return username;

    }
}
