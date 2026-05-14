package com.passport.creditpassport.creditpassport;

import com.passport.creditpassport.creditpassport.model.CreditPassport;

public interface CreditPassportQueryPort {
    CreditPassport getCreditPassport(String nationalId);
}
