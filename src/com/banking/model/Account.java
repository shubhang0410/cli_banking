package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private int accountId;
    private int userId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public Account() {}

    public Account(int userId, String accountNumber, String accountType) {
        this.userId        = userId;
        this.accountNumber = accountNumber;
        this.accountType   = accountType;
        this.balance       = BigDecimal.ZERO;
    }

    // Getters & Setters
    public int getAccountId()                           { return accountId; }
    public void setAccountId(int accountId)             { this.accountId = accountId; }

    public int getUserId()                              { return userId; }
    public void setUserId(int userId)                   { this.userId = userId; }

    public String getAccountNumber()                    { return accountNumber; }
    public void setAccountNumber(String accountNumber)  { this.accountNumber = accountNumber; }

    public String getAccountType()                      { return accountType; }
    public void setAccountType(String accountType)      { this.accountType = accountType; }

    public BigDecimal getBalance()                      { return balance; }
    public void setBalance(BigDecimal balance)          { this.balance = balance; }

    public LocalDateTime getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Account{number='%s', type='%s', balance=%.2f}",
                accountNumber, accountType, balance);
    }
}
