package com.passport.creditpassport.creditpassport;

import com.passport.creditpassport.auth.UsersRepository;
import com.passport.creditpassport.creditpassport.model.CreditPassport;
import com.passport.creditpassport.creditpassport.repository.CreditPassportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditPassportService {

    private final CreditPassportRepository creditPassportRepository;
    private final UsersRepository usersRepository;

    public CreditPassport getCreditPassport(String nationalId) {
        // 1. Confirm the user exists in the user's table.
        if (!usersRepository.existsByNatId(nationalId)) {
            throw new IllegalArgumentException("No user found with national ID: " + nationalId);
        }

        // 2. Fetch their credit passport.
        return creditPassportRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No credit passport found for national ID: " + nationalId));
    }
}
