package com.passport.creditpassport.creditpassport.creditserviceImpl;

import com.passport.creditpassport.creditpassport.CreditPassport;
import com.passport.creditpassport.creditpassport.CreditPassportQueryPort;
import com.passport.creditpassport.creditpassport.AuthUserLookupPort;
import com.passport.creditpassport.creditpassport.repository.CreditPassportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditPassportQueryPort {

    private final CreditPassportRepository creditPassportRepository;
    private final AuthUserLookupPort authUserLookupPort;

    @Override
    public CreditPassport getCreditPassport(String nationalId) {

        // 1. Confirm the user exists in the users table
        if (!authUserLookupPort.existsByNationalId(nationalId)) {
            throw new IllegalArgumentException("No user found with national ID: " + nationalId);
        }

        // 2. Fetch their credit passport
        return creditPassportRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No credit passport found for national ID: " + nationalId));
    }
}
