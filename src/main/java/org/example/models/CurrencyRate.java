package org.example.models;

import org.example.enums.CurrencyType;

import javax.persistence.*;

@Entity
@Table(name = "CurrencyRates")
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Column(nullable = false)
    private double currencyRate;

    public CurrencyRate(){}

    public CurrencyRate(CurrencyType currency, double currencyRate){
        this.currencyRate = currencyRate;
        this.currency = currency;
    }

    public long getId() {
        return id;
    }
    public CurrencyType getCurrency() {
        return currency;
    }
    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }
    public double getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(double currencyRate) {
        this.currencyRate = currencyRate;
    }

    @Override
    public String toString(){
        return "CurrencyRate{id="+id+", " +
                "currency='"+currency+"', " +
                "currencyRate="+currencyRate+", " +
                "}";
    }
}
