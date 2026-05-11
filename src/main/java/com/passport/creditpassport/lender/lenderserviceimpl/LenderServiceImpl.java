package com.passport.creditpassport.lender.lenderserviceimpl;

import com.passport.creditpassport.lender.lenderdto.LenderRequest;
import com.passport.creditpassport.lender.lenderdto.LenderResponse;
import com.passport.creditpassport.lender.lendermodel.Lender;
import com.passport.creditpassport.lender.lenderservice.LenderService;
import com.passport.creditpassport.lender.repository.LenderRepository;
import com.passport.creditpassport.lender.util.ApiKeyUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LenderServiceImpl implements LenderService {

    private final LenderRepository lenderRepository;

    @Transactional
    @Override
    public LenderResponse registerLender(LenderRequest request) {
        // Duplicate Checks
        if (lenderRepository.existsByCbkLicenseNo(request.cbkLicenseNo())) {
            throw new IllegalArgumentException("A lender with this CBK License Number already exists");
        }

        if (request.certificateOfIncorporation() != null &&
                lenderRepository.existsByCertificateOfIncorporation(request.certificateOfIncorporation())) {
            throw new IllegalArgumentException("A lender with this Certificate of Incorporation already exists");
        }

        if (lenderRepository.existsByRegBusinessAddress(request.regBusinessAddress())) {
            throw new IllegalArgumentException("A lender with this registered business address already exists");
        }

        if (request.nameApprovalProof() != null &&
                lenderRepository.existsByNameApprovalProof(request.nameApprovalProof())) {
            throw new IllegalArgumentException("A lender with this Name Approval Proof already exists");
        }

        if (request.memorandumArticlesOfAssociation() != null &&
                lenderRepository.existsByMemorandumArticlesOfAssociation(request.memorandumArticlesOfAssociation())) {
            throw new IllegalArgumentException("A lender with this Memorandum of Association already exists");
        }

        // Generate Api Key
        String apiKey = "cp_" + UUID.randomUUID().toString().replace("-", "");

        // Map request to entity
        Lender lender = new Lender();
        lender.setLenderName(request.lenderName());
        lender.setCbkLicenseNo(request.cbkLicenseNo());
        lender.setCertificateOfIncorporation(request.certificateOfIncorporation());
        lender.setMemorandumArticlesOfAssociation(request.memorandumArticlesOfAssociation());
        lender.setRegBusinessAddress(request.regBusinessAddress());
        lender.setNameApprovalProof(request.nameApprovalProof());
        lender.setApiKey(ApiKeyUtil.hash(apiKey));

        lenderRepository.save(lender);

        return LenderResponse.builder()
                .apiKey(apiKey)
                .message(String.format(
                                "%s registered successfully. Store your API key safely - it will not be shown again.",
                                request.lenderName()))
                .build();
    }
}
