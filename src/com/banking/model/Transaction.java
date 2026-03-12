package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private int transactionId;
    private int accountId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime transactionDate;

    public Transaction() {}

    public Transaction(int accountId, String transactionType, BigDecimal amount,
                       BigDecimal balanceAfter, String description) {
        this.accountId       = accountId;
        this.transactionType = transactionType;
        this.amount          = amount;
        this.balanceAfter    = balanceAfter;
        this.description     = description;
    }

    // Getters & Setters
    public int getTransactionId()                               { return transactionId; }
    public void setTransactionId(int transactionId)             { this.transactionId = transactionId; }

    public int getAccountId()                                   { return accountId; }
    public void setAccountId(int accountId)                     { this.accountId = accountId; }

    public String getTransactionType()                          { return transactionType; }
    public void setTransactionType(String transactionType)      { this.transactionType = transactionType; }

    public BigDecimal getAmount()                               { return amount; }
    public void setAmount(BigDecimal amount)                    { this.amount = amount; }

    public BigDecimal getBalanceAfter()                         { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter)        { this.balanceAfter = balanceAfter; }

    public String getDescription()                              { return description; }
    public void setDescription(String description)              { this.description = description; }

    public LocalDateTime getTransactionDate()                   { return transactionDate; }
    public void setTransactionDate(LocalDateTime date)          { this.transactionDate = date; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("| %-12s | %10.2f | %12.2f | %-25s | %s |",
                transactionType, amount, balanceAfter,
                (description != null ? description : "-"),
                (transactionDate != null ? transactionDate.format(fmt) : "-"));
    }
}
