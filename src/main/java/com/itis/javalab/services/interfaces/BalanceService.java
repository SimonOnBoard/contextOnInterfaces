package com.itis.javalab.services.interfaces;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.itis.javalab.context.annotations.Component;

@Component
public interface BalanceService {
    public Double getBalance(DecodedJWT jwt);
    public boolean checkAvaliableBalance(DecodedJWT jwt, Double price, Integer count);
    public void setBalance(DecodedJWT jwt, Double price, Integer count);
}
