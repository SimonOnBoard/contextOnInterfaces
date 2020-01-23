package com.itis.javalab.services.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dao.interfaces.UserDao;
import com.itis.javalab.services.interfaces.BalanceService;

public class BalanceServiceImpl implements BalanceService {
    @Autowired
    private UserDao userDao;

    @Override
    public Double getBalance(DecodedJWT jwt) {
        Long id = jwt.getClaim("id").asLong();
        return userDao.getBalance(id);
    }

    @Override
    public boolean checkAvaliableBalance(DecodedJWT jwt, Double price, Integer count) {
        Double balance = getBalance(jwt);
        Double amount = price * count;
        return balance >= amount;
    }

    @Override
    public void setBalance(DecodedJWT jwt, Double price, Integer count) {
        Double balance = getBalance(jwt);
        balance = balance - price * count;
        updateBalance(jwt,balance);
    }

    private void updateBalance(DecodedJWT jwt, Double balance) {
        Long id = jwt.getClaim("id").asLong();
        userDao.updateBalance(id,balance);
    }


}
