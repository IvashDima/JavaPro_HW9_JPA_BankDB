package org.example.dao;

import org.example.models.Account;
import org.example.models.User;

import javax.persistence.TypedQuery;
import java.util.List;

public class AccountDAO extends AbstractDAO<Account> {
    public AccountDAO() {
        super(Account.class);
    }
    public List<Account> viewAccountsByUser(User user) {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.user = : user", Account.class);
            query.setParameter("user", user);
            List<Account> res = query.getResultList();
            return res;
    }
}
