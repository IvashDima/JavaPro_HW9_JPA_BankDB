package org.example.models;

import org.example.enums.CurrencyType;

import javax.persistence.*;

@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    public Account(){}

    public Account(User user, double balance, CurrencyType currency){
        this.user = user;
        this.balance = balance;
        this.currency = currency;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance += balance;
    }
    public void updateBalance(double balance) {
        this.balance += balance;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    @Override
    public String toString(){
        return "Account{id="+id+", " +
                "user='"+user.getName()+"', " +
                "balance="+balance+", " +
                "currency="+currency+", " +
                "}";
    }
}
