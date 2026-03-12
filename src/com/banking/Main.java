package com.banking;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.service.BankingService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final BankingService service        = new BankingService();
    private static final Scanner        scanner        = new Scanner(System.in);
    private static User                 currentUser    = null;
    private static boolean              isAdmin        = false;

    private static final String ADMIN_EMAIL    = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            if (isAdmin) {
                running = showAdminMenu();
            } else if (currentUser == null) {
                running = showAuthMenu();
            } else {
                running = showMainMenu();
            }
        }
        System.out.println("\nThank you for using BankingApp. Goodbye!");
        scanner.close();
    }

    private static boolean showAuthMenu() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║         MAIN MENU            ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. Login                    ║");
        System.out.println("║  2. Register                 ║");
        System.out.println("║  0. Exit                     ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleLogin();
            case "2" -> handleRegister();
            case "0" -> { return false; }
            default  -> System.out.println("Invalid option. Try again.");
        }
        return true;
    }

    private static boolean showMainMenu() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.printf ("║  Welcome, %-19s║%n", currentUser.getFullName());
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. View Accounts            ║");
        System.out.println("║  2. Deposit                  ║");
        System.out.println("║  3. Withdraw                 ║");
        System.out.println("║  4. Transfer                 ║");
        System.out.println("║  5. Transaction History      ║");
        System.out.println("║  6. Logout                   ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleViewAccounts();
            case "2" -> handleDeposit();
            case "3" -> handleWithdraw();
            case "4" -> handleTransfer();
            case "5" -> handleHistory();
            case "6" -> { currentUser = null; System.out.println("Logged out."); }
            default  -> System.out.println("Invalid option.");
        }
        return true;
    }

    private static boolean showAdminMenu() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║      ADMIN DASHBOARD         ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. View All Users           ║");
        System.out.println("║  2. View All Accounts        ║");
        System.out.println("║  3. View All Transactions    ║");
        System.out.println("║  4. System Summary           ║");
        System.out.println("║  5. Logout                   ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> adminViewAllUsers();
            case "2" -> adminViewAllAccounts();
            case "3" -> adminViewAllTransactions();
            case "4" -> adminViewSummary();
            case "5" -> { isAdmin = false; System.out.println("Admin logged out."); }
            default  -> System.out.println("Invalid option.");
        }
        return true;
    }

    private static void handleLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            isAdmin = true;
            System.out.println("Admin login successful! Welcome, Admin.");
            return;
        }
        try {
            User user = service.login(email, password);
            if (user != null) {
                currentUser = user;
                System.out.println("Login successful! Welcome, " + user.getFullName());
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
    }

    private static void handleRegister() {
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        try {
            User user = service.register(name, email, password);
            if (user != null) {
                System.out.println("Registration successful! A SAVINGS account has been created for you.");
            } else {
                System.out.println("Registration failed. Email may already be in use.");
            }
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
        }
    }

    private static void handleViewAccounts() {
        try {
            List<Account> accounts = service.getUserAccounts(currentUser.getUserId());
            if (accounts.isEmpty()) { System.out.println("No accounts found."); return; }
            System.out.println("\n── Your Accounts ──────────────────────────────");
            for (Account acc : accounts) {
                System.out.printf("  Account No : %s%n", acc.getAccountNumber());
                System.out.printf("  Type       : %s%n", acc.getAccountType());
                System.out.printf("  Balance    : %.2f%n", acc.getBalance());
                System.out.println("  ───────────────────────────────────────────");
            }
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void handleDeposit() {
        System.out.print("Enter Account Number: ");
        String accNo = scanner.nextLine().trim();
        System.out.print("Enter Amount: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            System.out.print("Description (optional): ");
            String desc = scanner.nextLine().trim();
            System.out.println(service.deposit(accNo, amount, desc.isEmpty() ? "Deposit" : desc));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void handleWithdraw() {
        System.out.print("Enter Account Number: ");
        String accNo = scanner.nextLine().trim();
        System.out.print("Enter Amount: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            System.out.print("Description (optional): ");
            String desc = scanner.nextLine().trim();
            System.out.println(service.withdraw(accNo, amount, desc.isEmpty() ? "Withdrawal" : desc));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void handleTransfer() {
        System.out.print("From Account Number: ");
        String fromAcc = scanner.nextLine().trim();
        System.out.print("To Account Number: ");
        String toAcc = scanner.nextLine().trim();
        System.out.print("Amount: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            System.out.println(service.transfer(fromAcc, toAcc, amount));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void handleHistory() {
        System.out.print("Enter Account Number: ");
        String accNo = scanner.nextLine().trim();
        try {
            List<Transaction> history = service.getTransactionHistory(accNo);
            if (history.isEmpty()) { System.out.println("No transactions found."); return; }
            System.out.println("\n── Transaction History ──────────────────────────────────────────────────────────");
            System.out.printf("| %-12s | %10s | %12s | %-25s | %s |%n", "TYPE", "AMOUNT", "BAL AFTER", "DESCRIPTION", "DATE");
            System.out.println("────────────────────────────────────────────────────────────────────────────────────");
            for (Transaction t : history) System.out.println(t);
            System.out.println("────────────────────────────────────────────────────────────────────────────────────");
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void adminViewAllUsers() {
        try {
            List<User> users = service.adminGetAllUsers();
            System.out.println("\n── All Registered Users ──────────────────────────────────────");
            System.out.printf("| %-5s | %-20s | %-30s |%n", "ID", "NAME", "EMAIL");
            System.out.println("────────────────────────────────────────────────────────────────");
            for (User u : users)
                System.out.printf("| %-5d | %-20s | %-30s |%n", u.getUserId(), u.getFullName(), u.getEmail());
            System.out.println("────────────────────────────────────────────────────────────────");
            System.out.println("Total users: " + users.size());
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void adminViewAllAccounts() {
        try {
            List<String[]> accounts = service.adminGetAllAccounts();
            System.out.println("\n── All Accounts ──────────────────────────────────────────────────────────────────");
            System.out.printf("| %-14s | %-8s | %12s | %-20s | %-25s |%n", "ACCOUNT NO", "TYPE", "BALANCE", "OWNER", "EMAIL");
            System.out.println("─────────────────────────────────────────────────────────────────────────────────────");
            for (String[] row : accounts)
                System.out.printf("| %-14s | %-8s | %12s | %-20s | %-25s |%n", row[0], row[1], row[2], row[3], row[4]);
            System.out.println("─────────────────────────────────────────────────────────────────────────────────────");
            System.out.println("Total accounts: " + accounts.size());
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void adminViewAllTransactions() {
        try {
            List<Transaction> txns = service.adminGetAllTransactions();
            if (txns.isEmpty()) { System.out.println("No transactions found."); return; }
            System.out.println("\n── All Transactions ──────────────────────────────────────────────────────────────");
            System.out.printf("| %-12s | %10s | %12s | %-25s | %s |%n", "TYPE", "AMOUNT", "BAL AFTER", "DESCRIPTION", "DATE");
            System.out.println("──────────────────────────────────────────────────────────────────────────────────────");
            for (Transaction t : txns) System.out.println(t);
            System.out.println("──────────────────────────────────────────────────────────────────────────────────────");
            System.out.println("Total transactions: " + txns.size());
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void adminViewSummary() {
        try {
            List<User>     users    = service.adminGetAllUsers();
            List<String[]> accs     = service.adminGetAllAccounts();
            double         totalBal = service.adminGetTotalBalance();
            int            totalTxn = service.adminGetTotalTransactions();
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║           SYSTEM SUMMARY           ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.printf ("║  Total Users        : %-13d║%n", users.size());
            System.out.printf ("║  Total Accounts     : %-13d║%n", accs.size());
            System.out.printf ("║  Total Transactions : %-13d║%n", totalTxn);
            System.out.printf ("║  Total Balance      : %-13.2f║%n", totalBal);
            System.out.println("╚════════════════════════════════════╝");
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ██████╗  █████╗ ███╗   ██╗██╗  ██╗");
        System.out.println("  ██╔══██╗██╔══██╗████╗  ██║██║ ██╔╝");
        System.out.println("  ██████╔╝███████║██╔██╗ ██║█████╔╝ ");
        System.out.println("  ██╔══██╗██╔══██║██║╚██╗██║██╔═██╗ ");
        System.out.println("  ██████╔╝██║  ██║██║ ╚████║██║  ██╗");
        System.out.println("  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝");
        System.out.println("       Banking Transaction Manager v1.0");
        System.out.println("  ─────────────────────────────────────");
    }
}