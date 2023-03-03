package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.Map;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private final AccountService accountService = new AccountService(API_BASE_URL);

    private final TransferService transferService = new TransferService(accountService, API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        consoleService.printBalanceDisplay();
		System.out.println("\nTEnmo balance");
        System.out.println("  ‡" + accountService.viewBalance(currentUser));
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		System.out.println(transferService.getListOfTransfers(currentUser));
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        consoleService.printListOfUsersDisplay();
        consoleService.printUserLabelsDisplay();
        for(Account account : accountService.getListOfUsers(currentUser)) {
            System.out.println("  " + account.getUserId() + "         " + accountService.getUsernameByUserId(account.getUserId(), currentUser));
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
                amountSelection = consoleService.promptForBigDecimal("Enter amount:");
                transferService.transactionTEBucks(currentUser, idSelection, amountSelection);
                transferService.sendTEBucks(currentUser, amountSelection);
                transferService.ReceiveTEBucks(idSelection, amountSelection, currentUser);
                System.out.println("\nSending ‡" + amountSelection + " to " + accountService.getUsernameByUserId(idSelection,currentUser) + "!");
                break;
            } else {
                System.out.println("Invalid ID Selection");
            }
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}