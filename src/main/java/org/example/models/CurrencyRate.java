package org.example.models;

import javax.persistence.*;

@Entity
@Table(name = "CurrencyRates")
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
}
