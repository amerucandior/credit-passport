package com.passport.creditpassport.creditpassport;

import com.passport.creditpassport.auth.UsersRepository;
import com.passport.creditpassport.auth.models.User;
import com.passport.creditpassport.creditpassport.model.CreditPassport;
import com.passport.creditpassport.creditpassport.repository.CreditPassportRepository;
import com.passport.creditpassport.kyc.model.KYC;
import com.passport.creditpassport.kyc.repository.KycRepository;
import com.passport.creditpassport.scoring.dto.ScoringRequest;
import com.passport.creditpassport.scoring.dto.ScoringResponse;
import com.passport.creditpassport.scoring.mapper.ScoringRequestMapper;
import com.passport.creditpassport.scoring.service.ScoringServiceClient;
import com.passport.creditpassport.statement.dto.FinancialStatementResponse;
import com.passport.creditpassport.statement.service.FinancialStatementService;
import com.passport.creditpassport.userprofile.models.UserProfile;
import com.passport.creditpassport.userprofile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditPassportService {

    private final CreditPassportRepository creditPassportRepository;
    private final UsersRepository usersRepository;
    private final FinancialStatementService financialStatementService;
    private final ProfileRepository profileRepository;
    private final ScoringServiceClient scoringServiceClient;
    private final KycRepository kycRepository;
    private final ScoringRequestMapper scoringRequestMapper;

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

//  Generates user's creditpassport
    @Transactional
    public CreditPassport generateOrRefreshPassport(String nationalId) {
        User user = usersRepository.findByNatId(nationalId)
                .orElseThrow(() -> new IllegalArgumentException("No user found"));
        UserProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile required"));
        List<FinancialStatementResponse> statements =
                financialStatementService.getAllByUser(user.getId());

        boolean kycVerified = kycRepository.findByUserId(user.getId())
                .map(KYC::isVerified)
                .orElse(false);

        ScoringRequest request = scoringRequestMapper.toScoringRequest(
                user, profile, statements, kycVerified);
        ScoringResponse scored = scoringServiceClient.score(request);

        CreditPassport passport = creditPassportRepository.findByNationalId(nationalId)
                .orElseGet(CreditPassport::new);

        populatePassport(passport, user, profile, scored);
        return creditPassportRepository.save(passport);
    }

    private void populatePassport(
            CreditPassport passport,
            User user,
            UserProfile profile,
            ScoringResponse scored
    ) {
        if (passport.getUserId() == null) {
            passport.setUserId(user.getId());
        }

        passport.setNationalId(user.getNatId());
        passport.setName(user.getName());
        passport.setDateOfBirth(profile.getDateOfBirth());
        passport.setEmail(user.getEmail());
        passport.setPhoneNumber(user.getNumber());
        passport.setCreditScore(scored.creditScore());
        passport.setCreditScoreBand(scored.creditScoreBand());

        Instant issuedAt = Instant.now();
        passport.setIssuedAt(issuedAt);
        passport.setExpiresAt(issuedAt.plus(90, ChronoUnit.DAYS));
    }
}
