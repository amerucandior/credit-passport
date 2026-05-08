package com.passport.creditpassport.creditpassport.creditserviceImpl;

import com.passport.creditpassport.auth.models.User;

import com.passport.creditpassport.auth.repository.UsersRepository;
import com.passport.creditpassport.creditpassport.CreditPassport;
import com.passport.creditpassport.creditpassport.creditservice.CreditService;
import com.passport.creditpassport.creditpassport.repository.CreditPassportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditPassportRepository creditPassportRepository;
    private final UsersRepository usersRepository;

    @Override
    public CreditPassport getCreditPassport(String nationalId) {

        // 1. Confirm the user exists in the users table
        User user = usersRepository.findByNatId(nationalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No user found with national ID: " + nationalId));

        // 2. Fetch their credit passport
        return creditPassportRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No credit passport found for national ID: " + nationalId));
    }
}
