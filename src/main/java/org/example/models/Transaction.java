package org.example.models;

import org.example.enums.CurrencyType;
import org.example.enums.TransactionType;

import javax.persistence.*;

@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = true)
    private Account senderAccount;
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiverAccount;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    public Transaction(){}

    public Transaction(Account senderAccount, Account receiverAccount, double amount, TransactionType type){
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public Account getSender() {
        return senderAccount;
    }
    public void setSender(Account senderAccount) {
        this.senderAccount = senderAccount;
    }
    public Account getReceiver() {
        return receiverAccount;
    }
    public void setReceiver(Account receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public void updateAmount(double amount) {
        this.amount += amount;
    }

    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }

    @Override
    public String toString(){
        return "Transaction{id="+id+", " +
                "senderAccount="+senderAccount+", " +
                "receiverAccount="+receiverAccount+", " +
                "amount="+amount+", " +
                "type="+type+
                "}";
    }
}
