package org.example.dao;

import org.example.enums.CurrencyType;
import org.example.models.CurrencyRate;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class CurrencyRateDAO extends AbstractDAO<CurrencyRate> {
    public CurrencyRateDAO() {
        super(CurrencyRate.class);
    }
    public double getRateByCurrency(CurrencyType curr) {
        TypedQuery<CurrencyRate> query = em.createQuery(
                "SELECT c FROM CurrencyRate c WHERE c.currency = :curr", CurrencyRate.class);
        query.setParameter("curr", curr);
        List<CurrencyRate> res = query.getResultList();
        if(res.isEmpty()) throw new RuntimeException("Currency rate for "+curr+" not found!");
        return res.get(0).getRate();
    }
}
