package org.example;

import org.example.dao.*;
import org.example.enums.CurrencyType;
import org.example.enums.TransactionType;
import org.example.models.Account;
import org.example.models.CurrencyRate;
import org.example.models.Transaction;
import org.example.models.User;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start of program!");
        UserDAO userDAO = new UserDAO();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        CurrencyRateDAO currencyRateDAO = new CurrencyRateDAO();

        insertCurrencyRate(currencyRateDAO);
        currencyRateDAO.viewAll(CurrencyRate.class);

        Scanner sc = new Scanner(System.in);
        try{
            while (true) {
                System.out.println("\n1: insert random data");
                System.out.println("2: add user manually");
                System.out.println("3: view users");
                System.out.println("4: add accounts manually");
                System.out.println("5: view accounts");
                System.out.println("6: deposit account");
                System.out.println("7: transfer between accounts (in one currency)");
                System.out.println("8: transfer with convertion by currency rate");
                System.out.println("9: total balance");
                System.out.println("0: exit");
                System.out.print("-> ");

                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        User user = insertRandomUser(userDAO);
                        insertAccounts(accountDAO,user);
                        accountDAO.viewAll(Account.class);
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
                        transferWithoutConvert(accountDAO, transactionDAO, sc);
                        break;
                    case "8":
                        transferWithConvert(currencyRateDAO, userDAO, accountDAO, transactionDAO, sc);
                        break;
                    case "9":
                        totalAmountByUser(currencyRateDAO, userDAO, accountDAO, transactionDAO, sc);
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
        userDAO.close();
        accountDAO.close();
        transactionDAO.close();
        currencyRateDAO.close();
        AbstractDAO.closeFactory();
    }
    private static void addUser(UserDAO userDAO, Scanner sc) {
        System.out.print("Enter client name: ");
        String name = sc.nextLine();

        User usr = new User(name);
        userDAO.add(usr);
        System.out.println("User was added, ID = " + usr.getId() + ", name = "+usr.getName());
    }
    private static User insertRandomUser(UserDAO userDAO) {
        User usr = new User(randomName());
        userDAO.add(usr);
        System.out.println("User was added: ID = " + usr.getId() + ", name = "+usr.getName());
        return usr;
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
    private static void insertAccounts(AccountDAO accountDAO, User usr) {
        Account accountUSD = new Account(usr, 1000, CurrencyType.USD);
        accountDAO.add(accountUSD);
        Account accountEUR = new Account(usr, 1000, CurrencyType.EUR);
        accountDAO.add(accountEUR);
        Account accountUAH = new Account(usr, 1000, CurrencyType.UAH);
        accountDAO.add(accountUAH);
        System.out.println("Were added accounts in USD, EUR, UAH for user "+usr);
    }
    private static void insertCurrencyRate(CurrencyRateDAO curRateDAO) {
        CurrencyRate curRateUSD = new CurrencyRate(CurrencyType.USD, 41);
        curRateDAO.add(curRateUSD);
        CurrencyRate curRateEUR = new CurrencyRate(CurrencyType.EUR, 45);
        curRateDAO.add(curRateEUR);
        CurrencyRate curRateUAH = new CurrencyRate(CurrencyType.UAH, 1);
        curRateDAO.add(curRateUAH);
        System.out.println("Were added currency rate for USD, EUR, UAH");
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
    private static void transferWithoutConvert(AccountDAO accountDAO,TransactionDAO transactionDAO, Scanner sc) {
        System.out.println("The conversion will not be performed! You choose transfer in one currency.");
        System.out.print("Enter sender account id: ");
        String sSenderAccountId = sc.nextLine();
        long senderAccountId = Long.parseLong(sSenderAccountId);
        Account accSender = accountDAO.getById(Account.class,senderAccountId);

        Account accReceiver = null;
        do {
            System.out.print("Enter receiver account id (with the same currency): ");
            String sReceiverAccountId = sc.nextLine();
            long receiverAccountId = Long.parseLong(sReceiverAccountId);
            accReceiver = accountDAO.getById(Account.class,receiverAccountId);
            if (accReceiver.getCurrency() != accSender.getCurrency()) {
                System.out.println("Error: incorrect account. Choose account with the same currency!");
                accountDAO.viewAll(Account.class);
            }
        }while (accReceiver.getCurrency() != accSender.getCurrency());

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
    private static void transferWithConvert(CurrencyRateDAO currencyRateDAO, UserDAO userDAO,AccountDAO accountDAO, TransactionDAO transactionDAO, Scanner sc) {
        System.out.print("Enter user id: ");
        String sUserId = sc.nextLine();
        long userId = Long.parseLong(sUserId);
        User usr = userDAO.getById(User.class,userId);

        List<Account> userAccounts = accountDAO.viewAccountsByUser(usr);
        for (Account acc: userAccounts)
            System.out.println(acc);

        Account accSender = null;
        do {
            System.out.print("Enter sender account id: ");
            String sSenderAccountId = sc.nextLine();
            long senderAccountId = Long.parseLong(sSenderAccountId);
            accSender = accountDAO.getById(Account.class,senderAccountId);
            if (accSender.getUser() != usr || accSender.getUser() == null) {
                System.out.println("Error: incorrect account. Try again.");
            }
        }while (accSender.getUser() != usr || accSender.getUser() == null);

        Account accReceiver = null;
        do {
            System.out.print("Enter receiver account id: ");
            String sReceiverAccountId = sc.nextLine();
            long receiverAccountId = Long.parseLong(sReceiverAccountId);
            accReceiver = accountDAO.getById(Account.class,receiverAccountId);
            if (accReceiver.getUser() != usr || accReceiver.getUser() == null) {
                System.out.println("Error: incorrect account. Try again.");
            }
        }while (accReceiver.getUser() != usr || accReceiver.getUser() == null);

        System.out.println("The conversion will be performed at the exchange rate of the recipient's account currency!");
        System.out.print("Enter amount to transfer from sender account: ");
        String sAmount = sc.nextLine();
        double amount = Double.parseDouble(sAmount);

        double amountInBaseCurrency = convertionByRate(currencyRateDAO, accSender, amount, true);
        double amountInCurrencyReceiver = convertionByRate(currencyRateDAO, accReceiver, amountInBaseCurrency, false);


        accSender.withdraw(amount);
        accountDAO.update(accSender);
        System.out.println("New balance of sender account = "+accSender.getBalance());

        accReceiver.deposit(amountInCurrencyReceiver);
        accountDAO.update(accReceiver);
        System.out.println("New balance of receiver account = "+accReceiver.getBalance());

        Transaction trn = new Transaction(accSender, accReceiver, amount, TransactionType.convert);
        transactionDAO.add(trn);
        System.out.println("Transaction was added: " + trn);
    }
    private static double convertionByRate(CurrencyRateDAO currencyRateDAO, Account acc, double amount, boolean toBaseCurrency){
        CurrencyType curr =  acc.getCurrency();
        double rate = currencyRateDAO.getRateByCurrency(curr);
        double convertedAmount = 0;
        if(toBaseCurrency == true){
            convertedAmount = amount*rate;
        }else{
            convertedAmount = amount/rate;
        }
        return convertedAmount;
    }
    private static void totalAmountByUser(CurrencyRateDAO currencyRateDAO, UserDAO userDAO,AccountDAO accountDAO, TransactionDAO transactionDAO, Scanner sc) {
        System.out.print("Enter user id: ");
        String sUserId = sc.nextLine();
        long userId = Long.parseLong(sUserId);
        User usr = userDAO.getById(User.class,userId);

        double total = 0;
        List<Account> userAccounts = accountDAO.viewAccountsByUser(usr);
        for (Account acc: userAccounts){
            System.out.println(acc);
            if(acc.getCurrency()== CurrencyType.UAH){
                total += acc.getBalance();
            } else {
                total += convertionByRate(currencyRateDAO, acc, acc.getBalance(), true);
            }
        }

        System.out.println("Total amount by "+usr.getName()+" at the base rate (UAH) on all accounts is "+ total);
    }

    static final String[] NAMES = {"Dima", "Alex", "Ivan", "Petro", "John", "Martin"};
    static final Random RND = new Random();

    static String randomName() {
        return NAMES[RND.nextInt(NAMES.length)];
    }
}