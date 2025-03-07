package org.example.dao;

import org.example.models.Account;
import org.example.models.CurrencyRate;

public class CurrencyRateDAO extends AbstractDAO<CurrencyRate> {
    public CurrencyRateDAO() {
        super(CurrencyRate.class);
    }
}
