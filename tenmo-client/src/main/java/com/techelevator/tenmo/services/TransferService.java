package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

public class TransferService {

    private final String baseUrl;
    private final AccountService accountService;
    private final ConsoleService consoleService;
    private final RestTemplate restTemplate = new RestTemplate();

    public TransferService(AccountService accountService, ConsoleService consoleService, String url) {
        this.accountService = accountService;
        this.consoleService = consoleService;
        this.baseUrl = url;
    }

    public void transferFunction(AuthenticatedUser currentUser){
        consoleService.printTransferHistory();
        viewTransferList(currentUser);
        viewTransferDetail(currentUser);
    }

    public void pendingTransferFunction(AuthenticatedUser currentUser){
        consoleService.printPendingTransfer();
        viewPendingTransferList(currentUser);
    }

    public void transactionTEBucks(AuthenticatedUser currentUser, int selectedId , BigDecimal amount) {
        HttpEntity<Transfer> entity = newTransferDetail(currentUser, selectedId, amount);
        try {
            restTemplate.exchange(baseUrl + "transfers/myTransactions", HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void sendTEBucks(AuthenticatedUser currentUser, BigDecimal amount) {
        HttpEntity<Transfer> entity = newSendTransfer(amount, currentUser);
        try {
            restTemplate.exchange(baseUrl + "transfers/send", HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void sendPendingTEBucks(AuthenticatedUser currentUser, int selectedId, BigDecimal amount) {
            HttpEntity<Transfer> entity = newTransferDetail(currentUser,selectedId,amount);
            try {
                restTemplate.exchange(baseUrl + "transfers/pending", HttpMethod.POST, entity, Transfer.class);
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        }

    public void receiveTEBucks(int idSelection, BigDecimal amount, AuthenticatedUser currentUser) {
        HttpEntity<Transfer> entity = newReceiveTransfer(amount,idSelection,currentUser);
        try {
            restTemplate.exchange(baseUrl + "transfers/receive", HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void transferTransaction(AuthenticatedUser currentUser) {
        consoleService.printListOfUsersDisplay();
        consoleService.printUserLabelsDisplay();
        Map<Integer,Account> listOfAccounts = new TreeMap<>();

        for(Account account : accountService.getListOfUsers(currentUser)) {
            if (account.getUserId() != currentUser.getUser().getId()) {
                listOfAccounts.put(account.getUserId(), account);
            }
        }

        for (Account account : listOfAccounts.values()) {
            System.out.println("   " + account.getUserId() + "         " + accountService.getUsernameByUserId(account.getUserId(), currentUser).toUpperCase());
        }

        System.out.println("═════════ ∘◦♔◦∘ ═════════");
        int idSelection = -1;
        while (idSelection != 0) {
            idSelection = consoleService.promptForInt("\nENTER THE ID OF THE USER YOU WANT TO SEND TE BUCKS (0 TO CANCEL): ");
            if (idSelection == 0){
                break;
            }
            if (listOfAccounts.containsKey(idSelection)) {
                BigDecimal amountSelection;
                while (true) {
                    amountSelection = consoleService.promptForBigDecimal("\nENTER AMOUNT: ");
                    if (amountSelection.equals(BigDecimal.valueOf(0)) || amountSelection.signum() < 0) {
                        System.out.println("PLEASE ENTER AN AMOUNT GREATER THAN ‡0.00!");
                    }
                    else if (accountService.viewBalance(currentUser).compareTo(amountSelection) >= 0) {
                        transactionTEBucks(currentUser, idSelection, amountSelection);
                        sendTEBucks(currentUser, amountSelection);
                        receiveTEBucks(idSelection, amountSelection, currentUser);
                        System.out.println("\nSENDING ‡" + String.format("%,.2f", amountSelection.setScale(2)) + " TO " + accountService.getUsernameByUserId(idSelection, currentUser).toUpperCase() + "!");
                        break;
                    } else {
                        System.out.println("\nINSUFFICIENT FUNDS. YOU CAN SEND UP TO ‡" + String.format("%,.2f", accountService.viewBalance(currentUser)));
                    }
                }
                break;
            } else {
                System.out.println("\nINVALID SELECTION");
            }
        }
    }

    public void pendingTransferTransaction(AuthenticatedUser currentUser) {
        consoleService.printListOfUsersDisplay();
        consoleService.printUserLabelsDisplay();
        Map<Integer,Account> listOfAccounts = new TreeMap<>();

        for(Account account : accountService.getListOfUsers(currentUser)) {
            if (account.getUserId() != currentUser.getUser().getId()) {
                listOfAccounts.put(account.getUserId(), account);
            }
        }

        for (Account account : listOfAccounts.values()) {
            System.out.println("   " + account.getUserId() + "         " + accountService.getUsernameByUserId(account.getUserId(), currentUser).toUpperCase());
        }

        System.out.println("═════════ ∘◦♔◦∘ ═════════");
        int idSelection = -1;
        while (idSelection != 0) {
            idSelection = consoleService.promptForInt("\nENTER THE ID OF THE USER YOU WANT TO SEND A REQUEST TO (0 TO CANCEL): ");
            if (idSelection == 0){
                break;
            }
            if (listOfAccounts.containsKey(idSelection)) {
                BigDecimal amountSelection;
                while (true) {
                    amountSelection = consoleService.promptForBigDecimal("\nENTER AMOUNT: ");
                    if (amountSelection.equals(BigDecimal.valueOf(0)) || amountSelection.signum() < 0) {
                        System.out.println("PLEASE ENTER AN AMOUNT GREATER THAN ‡0.00!");
                    } else {
                        sendPendingTEBucks(currentUser, idSelection, amountSelection);
                        System.out.println("\nSENDING A REQUEST OF ‡" + String.format("%,.2f", amountSelection.setScale(2)) + " TO " + accountService.getUsernameByUserId(idSelection, currentUser).toUpperCase() + "!");
                        break;
                    }
                }
                break;
            } else {
                System.out.println("\nINVALID SELECTION");
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

    public void viewTransferList(AuthenticatedUser currentUser) {
        int currentUserId = currentUser.getUser().getId();
        int currentAccountId = accountService.getAccountIdByUserId(currentUserId, currentUser);

        System.out.println("\033[0;1m" + "  [ ID ]      [ USERNAME ]          [  AMOUNT  ]" + "\u001B[0m");
        for (Transfer transfer : getListOfTransfers(currentUser)) {
            int fromId = transfer.getAccountFromId();
            int toId = transfer.getAccountToId();
            if ((currentAccountId == fromId || currentAccountId == toId) && (transfer.getTransferStatusId() == 2 || transfer.getTransferStatusId() == 3)) {
                int userId = (accountService.getUserIdByAccountId(fromId, currentUser) == currentUser.getUser().getId() ? accountService.getUserIdByAccountId(toId, currentUser) : accountService.getUserIdByAccountId(fromId, currentUser));
                String transferFromOrTo = (accountService.getUserIdByAccountId(fromId, currentUser) == currentUserId ? "TO:   " : "FROM: ");
                String addOrSubtractAmount = (accountService.getUserIdByAccountId(fromId, currentUser) == currentUserId ? "\033[0;31m" + "- ‡" + String.format("%,.2f", transfer.getAmount()) + "\033[0m" : "\033[0;32m" + "+ ‡" + String.format("%,.2f", transfer.getAmount()) + "\033[0m");
                if(transfer.getTransferStatusId() == 2){
                    System.out.printf("   %-10s  %-21s %s", (transfer.getTransferId()), (transferFromOrTo + accountService.getUsernameByUserId(userId, currentUser).toUpperCase()), addOrSubtractAmount + "\n");
                }
                if(transfer.getTransferStatusId() == 3){
                    System.out.printf("   %-10s  %-21s %s", (transfer.getTransferId()), (transferFromOrTo + accountService.getUsernameByUserId(userId, currentUser).toUpperCase()), "x ‡" + transfer.getAmount() + "\n");
                }
            }
        }
    }

    public void viewTransferDetail(AuthenticatedUser currentUser) {
        System.out.println();
        int transferIdSelection = -1;
        int checkId = 0;
        int currentUserId = currentUser.getUser().getId();
        int currentAccountId = accountService.getAccountIdByUserId(currentUserId, currentUser);
            while (transferIdSelection != 0) {
                if (getListOfTransfers(currentUser).isEmpty()) {
                    System.out.println("NO TRANSFER HISTORY");
                    break;
                } else {
                    transferIdSelection = consoleService.promptForInt("PLEASE ENTER THE TRANSFER ID TO VIEW DETAILS (0 TO CANCEL): ");
                    if (transferIdSelection == 0) {
                        break;
                    }
                    for (Transfer transfer : getListOfTransfers(currentUser)) {
                        int fromId = accountService.getUserIdByAccountId(transfer.getAccountFromId(), currentUser);
                        int toId = accountService.getUserIdByAccountId(transfer.getAccountToId(), currentUser);
                        if (currentAccountId == transfer.getAccountFromId() || currentAccountId == transfer.getAccountToId()) {
                            if (transferIdSelection == transfer.getTransferId()) {
                                checkId = transfer.getTransferId();
                                consoleService.transferDetailSign();
                                System.out.println("\033[0;1m" + "\n[ ID ] " + "\u001B[0m" + transfer.getTransferId());
                                System.out.println("\033[0;1m" + "[ FROM ] "  + "\u001B[0m"+ accountService.getUsernameByUserId(fromId, currentUser).toUpperCase());
                                System.out.println("\033[0;1m" + "[ TO ] "  + "\u001B[0m"+ accountService.getUsernameByUserId(toId, currentUser).toUpperCase());
                                System.out.println("\033[0;1m" + "[ TYPE ] "  + "\u001B[0m"+ transfer.getTypeById().toUpperCase());
                                System.out.println("\033[0;1m" + "[ STATUS ] "  + "\u001B[0m"+ transfer.getStatusById().toUpperCase());
                                System.out.println("\033[0;1m" + "[ AMOUNT ]"  + "\u001B[0m" + " ‡" +  String.format("%,.2f", transfer.getAmount()) + "\n");
                            }
                        }
                    }
                    if (transferIdSelection != checkId) {
                        System.out.println("\nINVALID SELECTION\n");
                    }
                }
            }
    }

    public void viewPendingTransferList(AuthenticatedUser currentUser) {
        int currentUserId = currentUser.getUser().getId();
        int currentAccountId = accountService.getAccountIdByUserId(currentUserId, currentUser);

        Map<Integer,Transfer> listOfPendingTransfers = new TreeMap<>();

        System.out.println("\033[0;1m" + " [ ID ]         [ USERNAME ]         [  AMOUNT  ]" + "\u001B[0m");
        for (Transfer transfer : getListOfTransfers(currentUser)) {
            int fromId = transfer.getAccountFromId();
            int toId = transfer.getAccountToId();
            if ((currentAccountId == fromId) && transfer.getTransferStatusId() == 1) {
                listOfPendingTransfers.put(transfer.getTransferId(), transfer);
                System.out.printf("  %-10s  %-22s %s", (transfer.getTransferId()),"TO: " + (accountService.getUsernameByUserId(accountService.getUserIdByAccountId(toId, currentUser), currentUser).toUpperCase()), "- ‡"+ String.format("%,.2f", transfer.getAmount()) + "\n");
            }
            if ((currentAccountId == toId) && transfer.getTransferStatusId() == 1) {
                listOfPendingTransfers.put(transfer.getTransferId(), transfer);
                System.out.printf("  %-8s  %-24s %s", (transfer.getTransferId()),"FROM: " + (accountService.getUsernameByUserId(accountService.getUserIdByAccountId(fromId, currentUser), currentUser).toUpperCase()), "+ ‡"+ String.format("%,.2f", transfer.getAmount()) + "\n");
            }
        }

        int idSelected = consoleService.promptForInt("\nPLEASE ENTER A TRANSFER ID TO APPROVE, REJECT, OR VIEW DETAILS (YOUR REQUESTS) (0 TO CANCEL): ");
        if (idSelected == 0) {
        } else if (listOfPendingTransfers.containsKey(idSelected) && listOfPendingTransfers.get(idSelected).getAccountToId() != currentAccountId){
            pendingTransferChoice(currentUser, idSelected);
        } else {
            if (currentAccountId == listOfPendingTransfers.get(idSelected).getAccountFromId() || currentAccountId == listOfPendingTransfers.get(idSelected).getAccountToId()) {
                if (idSelected == listOfPendingTransfers.get(idSelected).getTransferId()) {
                    consoleService.transferDetailSign();
                    Transfer transfer = listOfPendingTransfers.get(idSelected);
                    System.out.println("\033[0;1m" + "\n[ ID ] " + "\u001B[0m" + transfer.getTransferId());
                    System.out.println("\033[0;1m" + "[ FROM ] "  + "\u001B[0m"+ accountService.getUsernameByUserId(accountService.getUserIdByAccountId(transfer.getAccountFromId(), currentUser), currentUser).toUpperCase());
                    System.out.println("\033[0;1m" + "[ TO ] "  + "\u001B[0m"+ accountService.getUsernameByUserId(accountService.getUserIdByAccountId(transfer.getAccountToId(), currentUser), currentUser).toUpperCase());
                    System.out.println("\033[0;1m" + "[ TYPE ] "  + "\u001B[0m"+ transfer.getTypeById().toUpperCase());
                    System.out.println("\033[0;1m" + "[ STATUS ] "  + "\u001B[0m"+ transfer.getStatusById().toUpperCase());
                    System.out.println("\033[0;1m" + "[ AMOUNT ]"  + "\u001B[0m" + " ‡" +  String.format("%,.2f", transfer.getAmount()) + "\n");
                }
            }
        }


    }

    public void pendingTransferChoice(AuthenticatedUser currentUser, int idSelected){

        System.out.println("\n[1] APPROVE");
        System.out.println("[2] REJECT");
        System.out.println("[0] GO BACK TO MAIN MENU\n");

        int menuSelection = -1;
        while (menuSelection != 0) {
            menuSelection = consoleService.promptForMenuSelection("PLEASE CHOOSE AN OPTION: ");
            if (menuSelection == 0) {
                break;
            }
            if (menuSelection == 1) {
                Transfer transfer = approvePendingTransfer(idSelected, currentUser);
                sendTEBucks(currentUser, transfer.getAmount());
                receiveTEBucks(accountService.getUserIdByAccountId(transfer.getAccountToId(),currentUser), transfer.getAmount(), currentUser);
                System.out.println("\nSENDING ‡" + String.format("%,.2f", transfer.getAmount().setScale(2)) + " TO " + accountService.getUsernameByUserId(accountService.getUserIdByAccountId(transfer.getAccountToId(), currentUser), currentUser).toUpperCase() + "!");
                break;
            } else if (menuSelection == 2) {
                rejectedPendingTransfer(idSelected, currentUser);
                System.out.println("\nYOU HAVE REJECTED THE PENDING REQUEST!");
                break;
            } else {
                System.out.println("\nINVALID SELECTION");
            }
            consoleService.pause();
        }
    }

    public Transfer approvePendingTransfer(int idSelection, AuthenticatedUser currentUser){
        Transfer transfer = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "transfers/approved/" + idSelection, HttpMethod.PUT, entity, Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    public Transfer rejectedPendingTransfer(int idSelection, AuthenticatedUser currentUser){
        Transfer transfer = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "transfers/rejected/" + idSelection, HttpMethod.PUT, entity, Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }


    public HttpEntity<Transfer> newReceiveTransfer(BigDecimal amount, int idSelection, AuthenticatedUser currentUser){
        Transfer receive = new Transfer();
        receive.setAccountToId(accountService.getAccountIdByUserId(idSelection,currentUser));
        receive.setAmount(amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(receive, headers);
        return entity;
    }

    public HttpEntity<Transfer> newSendTransfer(BigDecimal amount, AuthenticatedUser currentUser){
        Transfer sendPending = new Transfer();
        sendPending.setAccountFromId(accountService.getAccountIdByUserId(currentUser.getUser().getId(),currentUser));
        sendPending.setAmount(amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(sendPending, headers);
        return entity;
    }

    public HttpEntity<Transfer> newTransferDetail(AuthenticatedUser currentUser, int selectedId, BigDecimal amount){
        Transfer newTransfer = new Transfer();
        newTransfer.setAccountFromId(accountService.getAccountIdByUserId(currentUser.getUser().getId(),currentUser));
        newTransfer.setAccountToId(accountService.getAccountIdByUserId(selectedId, currentUser));
        newTransfer.setAmount(amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(newTransfer, headers);
        }
}
