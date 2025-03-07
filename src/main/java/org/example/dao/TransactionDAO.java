package org.example.dao;

import org.example.models.Transaction;

public class TransactionDAO extends AbstractDAO<Transaction> {
    public TransactionDAO() {
        super(Transaction.class);
    }
}
