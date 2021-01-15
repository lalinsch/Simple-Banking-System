package banking;

import sqlconnection.Database;

import java.util.Scanner;

public class UI {
    private final Database database;
    private final Scanner scanner;
    private String currentUser;

    public UI(Database database) {
        this.scanner = new Scanner(System.in);
        this.database = database;
    }

    public void start() {
        while (true) {
            System.out.println();
            printMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    createAccount();
                    continue;
                case "2":
                    login();
                    continue;
                case "0":
                    System.out.println("Bye!");
                    break;
                default:
                    System.out.println("Invalid input");
                    continue;
            }
            break;
        }
    }

    private void login() {
        System.out.println("Enter your card number:");
        String cardNumberInput = scanner.nextLine();
        String PINInput;
        while (cardNumberIsInvalid(cardNumberInput)) {
            cardNumberInput = scanner.nextLine();
        }
        System.out.println("Enter your PIN:");
        boolean pinIsCorrect = false;
        while (!pinIsCorrect) {
            PINInput = scanner.nextLine();
            if (PINInput.length() != 4) {
                System.out.println("PIN should be 4 digits! Try again:");
            } else if (!database.isCorrectPIN(cardNumberInput, PINInput)) {
                System.out.println("Incorrect PIN, try again:");
            } else {
                pinIsCorrect = true;
            }
        }
        this.currentUser = cardNumberInput;
        System.out.println("You have successfully logged in!\n");
        showAccountOptions();
    }

    private void showAccountOptions() {
        String command;
        while (true) {
            System.out.println("\n1. Balance\n2. Add income\n3. Do transfer\n4. Close account\n5. Log out\n0. Exit");
            command = scanner.nextLine();
            switch (command) {
                case "1":
                    getBalance();
                    continue;
                case "2":
                    addIncome();
                    continue;
                case "3":
                    doTransfer();
                    continue;
                case "4":
                    closeAccount();
                    break;
                case "5":
                    currentUser = "";
                    System.out.println("You have successfully logged out!");
                    break;
                case "0":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid input");
                    continue;
            }
            break;
        }
    }

    private boolean cardNumberIsInvalid(String cardNumber) {
        if (cardNumber.length() != 16 || !cardNumberFollowsLuhn(cardNumber)) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
        } else if (cardNumber.equals(currentUser)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!database.cardExists(cardNumber)) {
            System.out.println("Such a card does not exist");
        } else {
            return false;
        }
        return true;
    }

    private boolean cardNumberFollowsLuhn(String cardNumber) {
        int[] cardNumberIntArr = new int[cardNumber.length()];
        for (int i = 0; i < cardNumberIntArr.length; i++) {
            cardNumberIntArr[i] = cardNumber.charAt(i) - '0';
        }
        //Luhn algorithm
        int sum = 0;
        for (int i = 0; i < cardNumberIntArr.length - 1; i++) {
            if (i % 2 == 0) {
                cardNumberIntArr[i] *= 2;
            }
            if (cardNumberIntArr[i] > 9) {
                cardNumberIntArr[i] -= 9;
            }
            sum += cardNumberIntArr[i];
        }
        return (sum + cardNumberIntArr[cardNumberIntArr.length - 1]) % 10 == 0;
    }

    private void addIncome() {
        System.out.println("Enter income:");
        int income = Integer.parseInt(scanner.nextLine());
        database.addIncome(currentUser, income);
        System.out.println("Income was added!");
    }

    private void getBalance() {
        System.out.println("Balance: " + database.getBalance(currentUser));
    }

    private void closeAccount() {
        database.deleteAccount(currentUser);
        System.out.println("The account has been closed!");
    }

    private void doTransfer() {
        System.out.println("Enter card number:");
        String recipientCardNumber = scanner.nextLine();
        while (cardNumberIsInvalid(recipientCardNumber)) {
            recipientCardNumber = scanner.nextLine();
        }
        System.out.println("Enter how much money you want to transfer:");
        int amount = Integer.parseInt(scanner.nextLine());
        if (amount > database.getBalance(currentUser)) {
            System.out.println("Not enough money!");
        } else if (amount <= 0) {
            System.out.println("Amount can't be zero or less");
        } else {
            database.doTransfer(currentUser, recipientCardNumber, amount);
            System.out.println("Success!");
        }
    }

    private void createAccount() {
        Card card = new Card();
        System.out.println("Your card number:\n" + card.getNumber());
        System.out.println("Your card PIN:\n" + card.getPin());
        database.insert(card);
    }

    private void printMenu() {
        System.out.println("1. Create an account\n2. Log into account\n0. Exit");
    }
}
