package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransferService {

    private final AccountService accountService;

    private final String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public TransferService(AccountService accountService, String url) {
        this.accountService = accountService;
        this.baseUrl = url;
    }

    public void transactionTEBucks (AuthenticatedUser currentUser, int selectedId , BigDecimal amount) {
        Transfer newTransfer = new Transfer();
        newTransfer.setAccountFromId(accountService.getAccountById(currentUser.getUser().getId(), currentUser));
        newTransfer.setAccountToId(accountService.getAccountById(selectedId,currentUser));
        newTransfer.setAmount(amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(newTransfer, headers);

        try {restTemplate.exchange(baseUrl + "transfers/myTransactions", HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void sendTEBucks(AuthenticatedUser currentUser, BigDecimal amount){
        Transfer send = new Transfer();
        send.setAccountFromId(accountService.getAccountById(currentUser.getUser().getId(),currentUser));
        send.setAmount(amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(send, headers);
        try {
            restTemplate.exchange(baseUrl + "transfers/send", HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void ReceiveTEBucks(int idSelection, BigDecimal amount, AuthenticatedUser currentUser){
        Transfer receive = new Transfer();
        receive.setAccountToId(accountService.getAccountById(idSelection,currentUser));
        receive.setAmount(amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(receive, headers);
        try {
            restTemplate.exchange(baseUrl + "transfers/receive", HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public List<Transfer> getListOfTransfers(AuthenticatedUser currentUser) {
        List<Transfer> transferList = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer[]> entity = new HttpEntity<>(headers);
        try {
            transferList = Arrays.asList(restTemplate.exchange(baseUrl + "transfers/myTransactions", HttpMethod.GET, entity, Transfer[].class).getBody());
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferList;
    }
}
