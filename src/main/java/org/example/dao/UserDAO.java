package org.example.dao;

import org.example.models.Account;
import org.example.models.User;

public class UserDAO extends AbstractDAO<User>{
    public UserDAO() {
        super(User.class);
    }
}
