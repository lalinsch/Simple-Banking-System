package banking;

import java.util.Random;

public class Card {
    private String pin;
    private String number;
    private int balance;

    public Card() {
        generateCardNumber();
        generatePIN();
        balance = 0;
    }

    public void clear() {
        number = "";
        pin = "";
        balance = 0;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    //When a new card object gets initialised, this method ensures the number follows
    //a valid card number gets created following Luhn algorithm
    private void generateCardNumber() {
        Random random = new Random();
        int[] cardNumbers = {4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 6; i < 15; i++) {
            cardNumbers[i] = random.nextInt(10);
        }
        System.out.println();
        int sumOfCardDigits = luhnAlgorithm(cardNumbers);
        int checksum = 10 - (sumOfCardDigits % 10);
        cardNumbers[15] = checksum;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i : cardNumbers) {
            stringBuilder.append(i);
        }
        number = stringBuilder.toString();
    }

    private int luhnAlgorithm(int[] cardNumber) {
        int[] luhnArray = cardNumber.clone();
        int sum = 0;
        for (int i = 0; i < luhnArray.length; i++) {
            if (i % 2 == 0) {
                luhnArray[i] *= 2;
            }
            if (luhnArray[i] > 9) {
                luhnArray[i] -= 9;
            }
            sum += luhnArray[i];
        }
        return sum;
    }

    private void generatePIN() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            stringBuilder.append(random.nextInt(9));
        }
        setPin(stringBuilder.toString());
    }
}

