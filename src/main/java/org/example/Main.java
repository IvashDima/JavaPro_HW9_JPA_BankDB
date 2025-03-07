
//> * Создать базу данных «Банк» с таблицами «Пользователи», «Транзакции», «Счета» и «Курсы валют». Счет бывает 3-х видов:
//        USD, EUR, UAH. Написать запросы для пополнения счета в нужной валюте, перевода средств с одного счета на другой,
//        конвертации валюты по курсу в рамках счетов одного пользователя. Написать запрос для получения суммарных средств
//        на счету одного пользователя в UAH (расчет по курсу).

package org.example;

import org.example.dao.*;
import org.example.enums.CurrencyType;
import org.example.enums.TransactionType;
import org.example.models.Account;
import org.example.models.Transaction;
import org.example.models.User;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start of program!");
        UserDAO userDAO = new UserDAO();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        CurrencyRateDAO currencyRateDAO = new CurrencyRateDAO();

        Scanner sc = new Scanner(System.in);
        try{
            while (true) {
                System.out.println("1: add random users");
                System.out.println("2: add user manually");
                System.out.println("3: view users");
                System.out.println("4: add accounts manually");
                System.out.println("5: view accounts");
                System.out.println("6: update account (deposit)");
                System.out.println("7: update account (transfer)");
                System.out.println("8: convert by currency rate");
                System.out.println("9: total balance");
                System.out.println("0: exit");
                System.out.print("-> ");

                String s = sc.nextLine();
                switch (s) {
                    case "1":
//                        insertRandomUsers(userDAO, sc);
                        User user = new User("Dima");
                        userDAO.add(user);
                        insertAccounts(accountDAO,user,sc);
                        break;
                    case "2":
                        addUser(userDAO, sc);
                        break;
                    case "3":
                        userDAO.viewAll(User.class);
                        break;
                    case "4":
                        addAccount(accountDAO, userDAO, sc);
                        break;
                    case "5":
                        accountDAO.viewAll(Account.class);
                        break;
                    case "6":
                        deposit(accountDAO, transactionDAO, sc);
                        break;
                    case "7":
                        transfer(accountDAO, transactionDAO, sc);
                        break;
                    case "8":
                        convert(userDAO, transactionDAO, sc);
                        break;
                    case "9":
                        totalsum(userDAO, transactionDAO, sc);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Try again!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("End of program!");
        }
        AbstractDAO.closeFactory();
    }
    private static void addUser(UserDAO userDAO, Scanner sc) {
        System.out.print("Enter client name: ");
        String name = sc.nextLine();

        User usr = new User(name);
        userDAO.add(usr);
        System.out.println("User was added, ID = " + usr.getId() + ", name = "+usr.getName());
    }
    private static void insertRandomUsers(UserDAO userDAO, Scanner sc) {
        System.out.print("Enter users count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);

        System.out.println("Were added users:");
        for (int i = 0; i < count; i++) {
            User usr = new User(randomName());
            userDAO.add(usr);
            System.out.println("ID = " + usr.getId() + ", name = "+usr.getName());
        }
    }
    private static void addAccount(AccountDAO accountDAO, UserDAO userDAO, Scanner sc) {
        System.out.print("Enter user id: ");
        String sUserId = sc.nextLine();
        long userId = Long.parseLong(sUserId);
        User usr = userDAO.getById(User.class,userId);

        CurrencyType currency = null;
        while (currency == null) {
            System.out.print("Enter currency (USD, EUR, UAH): ");
            String sCurrency = sc.nextLine();
            currency = CurrencyType.fromString(sCurrency);
            if (currency == null) {
                System.out.println("Error: incorrect value. Try again.");
            }
        }
        System.out.print("Enter balance: ");
        String sBalance = sc.nextLine();
        double balance = Double.parseDouble(sBalance);

        Account account = new Account(usr, balance, currency);
        accountDAO.add(account);
        System.out.println("Account was added, ID = " + account.getId() + ", balance = "+account.getBalance());
    }
    private static void insertAccounts(AccountDAO accountDAO, User usr, Scanner sc) {
        System.out.println("Were added USD, EUR, UAH accounts for user "+usr);
        Account accountUSD = new Account(usr, 0, CurrencyType.USD);
        accountDAO.add(accountUSD);
        Account accountEUR = new Account(usr, 0, CurrencyType.EUR);
        accountDAO.add(accountEUR);
        Account accountUAH = new Account(usr, 0, CurrencyType.UAH);
        accountDAO.add(accountUAH);
    }
    private static void deposit(AccountDAO accountDAO,TransactionDAO transactionDAO, Scanner sc) {
        System.out.print("Enter account id: ");
        String sAccountId = sc.nextLine();
        long accountId = Long.parseLong(sAccountId);
        Account account = accountDAO.getById(Account.class,accountId);

        System.out.print("Enter amount: ");
        String sAmount = sc.nextLine();
        double amount = Double.parseDouble(sAmount);

        account.deposit(amount);
        accountDAO.update(account);
        System.out.println("New balance = "+account.getBalance());

        Transaction trn = new Transaction(null, account, amount, TransactionType.deposit);
        transactionDAO.add(trn);
        System.out.println("Transaction was added: " + trn);
    }
    private static void transfer(AccountDAO accountDAO,TransactionDAO transactionDAO, Scanner sc) {
        System.out.print("Enter sender account id: ");
        String sSenderAccountId = sc.nextLine();
        long senderAccountId = Long.parseLong(sSenderAccountId);
        Account accSender = accountDAO.getById(Account.class,senderAccountId);

        System.out.print("Enter receiver account id: ");
        String sReceiverAccountId = sc.nextLine();
        long receiverAccountId = Long.parseLong(sReceiverAccountId);
        Account accReceiver = accountDAO.getById(Account.class,receiverAccountId);

        System.out.print("Enter amount: ");
        String sAmount = sc.nextLine();
        double amount = Double.parseDouble(sAmount);

        accSender.withdraw(amount);
        accountDAO.update(accSender);
        System.out.println("New balance of sender = "+accSender.getBalance());

        accReceiver.deposit(amount);
        accountDAO.update(accReceiver);
        System.out.println("New balance of receiver = "+accReceiver.getBalance());

        Transaction trn = new Transaction(accSender, accReceiver, amount, TransactionType.transfer);
        transactionDAO.add(trn);
        System.out.println("Transaction was added: " + trn);
    }
    private static void convert(UserDAO userDAO,TransactionDAO transactionDAO, Scanner sc) {
        System.out.print("Enter user id: ");
        String sUserId = sc.nextLine();
        long userId = Long.parseLong(sUserId);
        User usr = userDAO.getById(User.class,userId);

        System.out.print("Enter receiver account id: ");
        String sReceiverAccountId = sc.nextLine();
        long receiverAccountId = Long.parseLong(sReceiverAccountId);
        Account accReceiver = accountDAO.getById(Account.class,receiverAccountId);

        System.out.print("Enter amount: ");
        String sAmount = sc.nextLine();
        double amount = Double.parseDouble(sAmount);

        accSender.withdraw(amount);
        accountDAO.update(accSender);
        System.out.println("New balance of sender = "+accSender.getBalance());

        accReceiver.deposit(amount);
        accountDAO.update(accReceiver);
        System.out.println("New balance of receiver = "+accReceiver.getBalance());

        Transaction trn = new Transaction(accSender, accReceiver, amount, TransactionType.transfer);
        transactionDAO.add(trn);
        System.out.println("Transaction was added: " + trn);
    }
    private static void totalsum(UserDAO userDAO,TransactionDAO transactionDAO, Scanner sc) {
        System.out.print("Enter sender account id: ");
        String sSenderAccountId = sc.nextLine();
        long senderAccountId = Long.parseLong(sSenderAccountId);
        Account accSender = accountDAO.getById(Account.class,senderAccountId);

        System.out.print("Enter receiver account id: ");
        String sReceiverAccountId = sc.nextLine();
        long receiverAccountId = Long.parseLong(sReceiverAccountId);
        Account accReceiver = accountDAO.getById(Account.class,receiverAccountId);

        System.out.print("Enter amount: ");
        String sAmount = sc.nextLine();
        double amount = Double.parseDouble(sAmount);

        accSender.withdraw(amount);
        accountDAO.update(accSender);
        System.out.println("New balance of sender = "+accSender.getBalance());

        accReceiver.deposit(amount);
        accountDAO.update(accReceiver);
        System.out.println("New balance of receiver = "+accReceiver.getBalance());

        Transaction trn = new Transaction(accSender, accReceiver, amount, TransactionType.transfer);
        transactionDAO.add(trn);
        System.out.println("Transaction was added: " + trn);
    }

    static final String[] NAMES = {"Dima", "Alex", "Ivan", "Petro", "John", "Martin"};
    static final Random RND = new Random();

    static String randomName() {
        return NAMES[RND.nextInt(NAMES.length)];
    }

}