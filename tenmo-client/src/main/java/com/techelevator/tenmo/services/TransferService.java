package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
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

public class TransferService {

    private final AccountService accountService;
    private final ConsoleService consoleService;

    private final String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public TransferService(AccountService accountService, ConsoleService consoleService, String url) {
        this.accountService = accountService;
        this.consoleService = consoleService;
        this.baseUrl = url;
    }

    public void transactionTEBucks (AuthenticatedUser currentUser, int selectedId , BigDecimal amount) {
        Transfer newTransfer = new Transfer();
        newTransfer.setAccountFromId(accountService.getAccountIdByUserId(currentUser.getUser().getId(), currentUser));
        newTransfer.setAccountToId(accountService.getAccountIdByUserId(selectedId,currentUser));
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
        send.setAccountFromId(accountService.getAccountIdByUserId(currentUser.getUser().getId(),currentUser));
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
        receive.setAccountToId(accountService.getAccountIdByUserId(idSelection,currentUser));
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

    public void transferTransaction(AuthenticatedUser currentUser){
        for(Account account : accountService.getListOfUsers(currentUser)) {
            if (account.getUserId() != currentUser.getUser().getId()) {
                System.out.println("  " + account.getUserId() + "         " + accountService.getUsernameByUserId(account.getUserId(), currentUser));
            }
        }
        System.out.println("═════════ ∘◦♔◦∘ ═════════");
        int idSelection = -1;
        int checkId = 0;
        while (idSelection != 0) {
            idSelection = consoleService.promptForInt("\nEnter ID of user you are sending to (0 to cancel): ");
            if (idSelection == 0){
                break;
            }
            for(Account account : accountService.getListOfUsers(currentUser)) {
                if(idSelection == account.getUserId()) {
                    checkId = account.getUserId();
                }
            }
            if (idSelection == checkId) {
                BigDecimal amountSelection;
                while (true) {
                    amountSelection = consoleService.promptForBigDecimal("Enter amount:");
                    if (amountSelection.equals(BigDecimal.valueOf(0)) || amountSelection.signum() < 0) {
                        System.out.println("Please enter an amount that's more than ‡0.00");
                    }
                    else if (accountService.viewBalance(currentUser).compareTo(amountSelection) >= 0) {
                        transactionTEBucks(currentUser, idSelection, amountSelection);
                        sendTEBucks(currentUser, amountSelection);
                        ReceiveTEBucks(idSelection, amountSelection, currentUser);
                        System.out.println("\nSending ‡" + amountSelection.setScale(2) + " to " + accountService.getUsernameByUserId(idSelection, currentUser) + "!");
                        break;
                    } else {
                        System.out.println("You can send up to ‡" + accountService.viewBalance(currentUser));
                    }
                }
                break;
            } else {
                System.out.println("Invalid ID Selection");
            }
        }
    }

    public List<Transfer> getListOfTransfers(AuthenticatedUser currentUser) {
        List<Transfer> transferList = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer[]> entity = new HttpEntity<>(headers);
        try {
            transferList = Arrays.asList(restTemplate.exchange(baseUrl + "transfers", HttpMethod.GET, entity, Transfer[].class).getBody());
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferList;
    }

    public void viewTransferList(AuthenticatedUser currentUser, int currentAccountId, int currentUserId) {
        for(Transfer transfer : getListOfTransfers(currentUser)) {
            if (getListOfTransfers(currentUser).isEmpty()) {
                System.out.println("\nNo Transfer History");
            } else {

                int fromId = transfer.getAccountFromId();
                int toId = transfer.getAccountToId();
                if (currentAccountId == fromId || currentAccountId == toId){
                    int userId = (accountService.getUserIdByAccountId(transfer.getAccountFromId(), currentUser) == currentUser.getUser().getId() ? accountService.getUserIdByAccountId(toId, currentUser) : accountService.getUserIdByAccountId(transfer.getAccountFromId(), currentUser));
                    String transferFromOrTo = (accountService.getUserIdByAccountId(fromId, currentUser) == currentUserId ? "To: " : "From: ");
                    String addOrSubtracting = (accountService.getUserIdByAccountId(fromId, currentUser) == currentUserId ? "- ‡" : "+ ‡");
                    System.out.print(transfer.getTransferId() + "        ");
                    System.out.print(transferFromOrTo + accountService.getUsernameByUserId(userId, currentUser) + "          ");
                    System.out.print(addOrSubtracting + transfer.getAmount() + "\n");
                }
            }
        }
    }

    public void viewTransferDetail(AuthenticatedUser currentUser, int currentAccountId, int currentUserId) {
        System.out.println();
        int transferIdSelection = -1;
        int checkId = 0;

        while (transferIdSelection != 0) {
            transferIdSelection = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
            if (transferIdSelection == 0) {
                break;
            }
            for (Transfer transfer : getListOfTransfers(currentUser)) {
                int fromId = accountService.getUserIdByAccountId(transfer.getAccountFromId(), currentUser);
                int toId = accountService.getUserIdByAccountId(transfer.getAccountToId(), currentUser);
                if (currentAccountId == transfer.getAccountFromId() || currentAccountId == transfer.getAccountToId()) {
                    if (transferIdSelection == transfer.getTransferId()) {
                        checkId = transfer.getTransferId();
                        System.out.println("Id: " + transfer.getTransferId());
                        System.out.println("From: " + accountService.getUsernameByUserId(fromId, currentUser));
                        System.out.println("To: " + accountService.getUsernameByUserId(toId, currentUser));
                        System.out.println("Type: " + transfer.getTypeById(transfer.getTransferTypeId()));
                        System.out.println("Status: " + transfer.getStatusById(transfer.getTransferStatusId()));
                        System.out.println("Amount: ‡"+transfer.getAmount());
                    }
                }
            }
            if (transferIdSelection != checkId) {
                System.out.println("Invalid ID Selection!");
            }
        }
    }
}
