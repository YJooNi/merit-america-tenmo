package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("╔════════ ∘◦♔◦∘ ════════╗");
        System.out.println("\033[0;1m" + "     WELCOME TO TENMO " + "\u001B[0m");
        System.out.println("╚════════ ∘◦♔◦∘ ════════╝");
    }

    public void printBalanceDisplay(){
        System.out.println("\n╔════════ ∘◦♔◦∘ ════════╗");
        System.out.println("\033[0;1m" + "      CURRENT BALANCE " + "\u001B[0m");
        System.out.println("╚════════ ∘◦♔◦∘ ════════╝");
    }

    public void printListOfUsersDisplay(){
        System.out.println("\n╔════════ ∘◦♔◦∘ ════════╗");
        System.out.println("\033[0;1m" + "       LIST OF USERS " + "\u001B[0m");
        System.out.println("╚════════ ∘◦♔◦∘ ════════╝");
    }

    public void printUserLabelsDisplay(){
        System.out.println("\033[0;1m" + "  [ ID ]       [ Name ]" + "\u001B[0m");
    }

    public void printTransferHistory(){
        System.out.println("\n╔════════════════════ ∘◦♔◦∘ ════════════════════╗");
        System.out.println("\033[0;1m" + "                  PAST TRANSFERS " + "\u001B[0m");
        System.out.println("╚════════════════════ ∘◦♔◦∘ ════════════════════╝");
    }

    public void printPendingTransfer(){
        System.out.println("\n╔════════════════════ ∘◦♔◦∘ ════════════════════╗");
        System.out.println("\033[0;1m" + "                 PENDING TRANSFERS " + "\u001B[0m");
        System.out.println("╚════════════════════ ∘◦♔◦∘ ════════════════════╝");
    }

    public void transferDetailSign(){
        System.out.println("\n╔════════ ∘◦♔◦∘ ════════╗");
        System.out.println("\033[0;1m" + "     TRANSFER DETAIL " + "\u001B[0m");
        System.out.println("╚════════ ∘◦♔◦∘ ════════╝");
    }

    public void printLoginPage(int counter){
        if (counter > 0){
            System.out.println("\n╔════════ ∘◦♔◦∘ ════════╗");
            System.out.println("\033[0;1m" + "        LOGIN PAGE " + "\u001B[0m");
            System.out.println("╚════════ ∘◦♔◦∘ ════════╝");
        }
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("[1] LOGIN");
        System.out.println("[2] REGISTER");
        System.out.println("[0] EXIT");
        System.out.println();
    }

    public void printMainMenu(AuthenticatedUser user) {

        String currentUsername = user.getUser().getUsername().toUpperCase();

        System.out.println("\n╔════════ ∘◦♔◦∘ ════════╗");
        System.out.println("\033[0;1m" + "        MAIN MENU " + "\u001B[0m");
        System.out.println("╚════════ ∘◦♔◦∘ ════════╝");
        System.out.println();
        System.out.println("WELCOME " + currentUsername + "!\n");
        System.out.println("[1] CHECK YOUR BALANCE");
        System.out.println("[2] VIEW PAST TRANSFERS");
        System.out.println("[3] VIEW PENDING REQUESTS");
        System.out.println("[4] SEND TE BUCKS");
        System.out.println("[5] REQUEST TE BUCKS");
        System.out.println("[0] SIGN OUT");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("\nUSERNAME: ");
        String password = promptForString("PASSWORD: ");
        return new UserCredentials(username, password);
    }

    public void signInSign(){
        System.out.println("\n╔════════ ∘◦♔◦∘ ════════╗");
        System.out.println("\033[0;1m" + "         SIGN IN " + "\u001B[0m");
        System.out.println("╚════════ ∘◦♔◦∘ ════════╝");
    }

    public void registerSign(){
        System.out.println("\n╔════════ ∘◦♔◦∘ ════════╗");
        System.out.println("\033[0;1m" + "          REGISTER " + "\u001B[0m");
        System.out.println("╚════════ ∘◦♔◦∘ ════════╝");
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("\nPLEASE ENTER A NUMBER: " );
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("\nPLEASE ENTER A DECIMAL NUMBER: ");
            }
        }
    }

    public void pause() {
        System.out.print("\nPRESS ENTER TO CONTINUE...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("\nAN ERROR HAS OCCURRED. CHECK THE LOGS FOR DETAILS.");
    }

}
