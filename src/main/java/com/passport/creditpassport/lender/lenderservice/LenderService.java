package com.passport.creditpassport.lender.lenderservice;

import com.passport.creditpassport.lender.lenderdto.LenderRequest;
import com.passport.creditpassport.lender.lenderdto.LenderResponse;

public interface LenderService {
    LenderResponse registerLender(LenderRequest request);
}
