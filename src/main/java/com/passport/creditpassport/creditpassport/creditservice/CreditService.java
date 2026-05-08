package com.passport.creditpassport.creditpassport.creditservice;

import com.passport.creditpassport.creditpassport.CreditPassport;

public interface CreditService {
    CreditPassport getCreditPassport(String nationalId);
}
