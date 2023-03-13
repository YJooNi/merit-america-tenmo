package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(consoleService, API_BASE_URL);
    private final TransferService transferService = new TransferService(accountService, consoleService, API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        int counter = 0;
        while (true) {
            consoleService.printLoginPage(counter);
            counter = 1;
            loginMenu();
            if (currentUser != null) {
                mainMenu();
            }
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("PLEASE CHOOSE AN OPTION: ");
            if (menuSelection == 0) {
                System.exit(0);
            }
            if (menuSelection == 1) {
                consoleService.signInSign();
                handleLogin();
            } else if (menuSelection == 2) {
                consoleService.registerSign();
                handleRegister();
                consoleService.printLoginPage(1);
            } else if (menuSelection != 0) {
                System.out.println("\nINVALID SELECTION");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("\nPLEASE REGISTER A NEW USER ACCOUNT");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("\nREGISTRATION SUCCESSFUL. YOU CAN NOW LOGIN.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
            consoleService.printLoginPage(1);
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu(currentUser);
            menuSelection = consoleService.promptForMenuSelection("PLEASE CHOOSE AN OPTION: ");
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
                currentUser =  null;
                break;
            } else {
                System.out.println("\nINVALID SELECTION");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        accountService.printBalance(currentUser);
	}

	private void viewTransferHistory() {
        transferService.transferFunction(currentUser);
	}

	private void viewPendingRequests() {
		transferService.pendingTransferFunction(currentUser);
	}

	private void sendBucks() {
        transferService.transferTransaction(currentUser);
    }

	private void requestBucks() {
		transferService.pendingTransferTransaction(currentUser);
	}

}
