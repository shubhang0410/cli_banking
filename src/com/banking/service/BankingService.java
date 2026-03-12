package com.banking.service;

import com.banking.dao.AccountDAO;
import com.banking.dao.TransactionDAO;
import com.banking.dao.UserDAO;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class BankingService {

    private final UserDAO        userDAO        = new UserDAO();
    private final AccountDAO     accountDAO     = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // ─── User Operations ───────────────────────────────────────────────

    public User register(String fullName, String email, String password) throws SQLException {
        User user = new User(fullName, email, password);
        boolean created = userDAO.registerUser(user);
        if (created) {
            user = userDAO.login(email, password);
            if (user != null) createAccount(user.getUserId(), "SAVINGS");
        }
        return user;
    }

    public User login(String email, String password) throws SQLException {
        return userDAO.login(email, password);
    }

    // ─── Account Operations ────────────────────────────────────────────

    public Account createAccount(int userId, String accountType) throws SQLException {
        String accNumber = "ACC" + String.format("%010d", (long)(Math.random() * 9_000_000_000L + 1_000_000_000L));
        Account account  = new Account(userId, accNumber, accountType);
        accountDAO.createAccount(account);
        return accountDAO.findByAccountNumber(accNumber);
    }

    public List<Account> getUserAccounts(int userId) throws SQLException {
        return accountDAO.findByUserId(userId);
    }

    public Account getAccount(String accountNumber) throws SQLException {
        return accountDAO.findByAccountNumber(accountNumber);
    }

    // ─── Transaction Operations ────────────────────────────────────────

    public String deposit(String accountNumber, BigDecimal amount, String description) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            return "ERROR: Amount must be greater than zero.";
        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) return "ERROR: Account not found.";
        BigDecimal newBalance = account.getBalance().add(amount);
        accountDAO.updateBalance(account.getAccountId(), newBalance);
        transactionDAO.recordTransaction(new Transaction(account.getAccountId(), "DEPOSIT", amount, newBalance, description));
        return String.format("SUCCESS: Deposited %.2f. New balance: %.2f", amount, newBalance);
    }

    public String withdraw(String accountNumber, BigDecimal amount, String description) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            return "ERROR: Amount must be greater than zero.";
        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) return "ERROR: Account not found.";
        if (account.getBalance().compareTo(amount) < 0)
            return "ERROR: Insufficient funds. Available: " + account.getBalance();
        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountDAO.updateBalance(account.getAccountId(), newBalance);
        transactionDAO.recordTransaction(new Transaction(account.getAccountId(), "WITHDRAWAL", amount, newBalance, description));
        return String.format("SUCCESS: Withdrew %.2f. New balance: %.2f", amount, newBalance);
    }

    public String transfer(String fromAccNo, String toAccNo, BigDecimal amount) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            return "ERROR: Amount must be greater than zero.";
        if (fromAccNo.equals(toAccNo))
            return "ERROR: Cannot transfer to the same account.";
        Account from = accountDAO.findByAccountNumber(fromAccNo);
        Account to   = accountDAO.findByAccountNumber(toAccNo);
        if (from == null) return "ERROR: Source account not found.";
        if (to   == null) return "ERROR: Destination account not found.";
        if (from.getBalance().compareTo(amount) < 0)
            return "ERROR: Insufficient funds. Available: " + from.getBalance();
        BigDecimal fromNew = from.getBalance().subtract(amount);
        accountDAO.updateBalance(from.getAccountId(), fromNew);
        transactionDAO.recordTransaction(new Transaction(from.getAccountId(), "TRANSFER", amount, fromNew, "Transfer to " + toAccNo));
        BigDecimal toNew = to.getBalance().add(amount);
        accountDAO.updateBalance(to.getAccountId(), toNew);
        transactionDAO.recordTransaction(new Transaction(to.getAccountId(), "TRANSFER", amount, toNew, "Transfer from " + fromAccNo));
        return String.format("SUCCESS: Transferred %.2f from %s to %s.", amount, fromAccNo, toAccNo);
    }

    // ─── History ───────────────────────────────────────────────────────

    public List<Transaction> getTransactionHistory(String accountNumber) throws SQLException {
        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) return List.of();
        return transactionDAO.findByAccountId(account.getAccountId());
    }

    public List<Transaction> getRecentTransactions(String accountNumber, int limit) throws SQLException {
        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) return List.of();
        return transactionDAO.findRecentTransactions(account.getAccountId(), limit);
    }

    // ─── Admin Operations ──────────────────────────────────────────────

    public List<User> adminGetAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public List<String[]> adminGetAllAccounts() throws SQLException {
        return accountDAO.getAllAccountsWithOwner();
    }

    public List<Transaction> adminGetAllTransactions() throws SQLException {
        return transactionDAO.getAllTransactions();
    }

    public double adminGetTotalBalance() throws SQLException {
        return accountDAO.getTotalBalance();
    }

    public int adminGetTotalTransactions() throws SQLException {
        return transactionDAO.getTotalTransactionCount();
    }
}