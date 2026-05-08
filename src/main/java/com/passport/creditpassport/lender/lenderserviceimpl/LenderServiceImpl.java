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
        if (lenderRepository.existsByCbkLicenseNo(request.getCbkLicenseNo())) {
            throw new IllegalArgumentException("A lender with this CBK License Number already exists");
        }

        if (request.getCertificateOfIncorporation() != null &&
                lenderRepository.existsByCertificateOfIncorporation(request.getCertificateOfIncorporation())) {
            throw new IllegalArgumentException("A lender with this Certificate of Incorporation already exists");
        }

        if (lenderRepository.existsByRegBusinessAddress(request.getRegBusinessAddress())) {
            throw new IllegalArgumentException("A lender with this registered business address already exists");
        }

        if (request.getNameApprovalProof() != null &&
                lenderRepository.existsByNameApprovalProof(request.getNameApprovalProof())) {
            throw new IllegalArgumentException("A lender with this Name Approval Proof already exists");
        }

        if (request.getMemorandumArticlesOfAssociation() != null &&
                lenderRepository.existsByMemorandumArticlesOfAssociation(request.getMemorandumArticlesOfAssociation())) {
            throw new IllegalArgumentException("A lender with this Memorandum of Association already exists");
        }

        // Generate Api Key
        String apiKey = "cp_" + UUID.randomUUID().toString().replace("-", "");

        // Map request to entity
        Lender lender = new Lender();
        lender.setLenderName(request.getLenderName());
        lender.setCbkLicenseNo(request.getCbkLicenseNo());
        lender.setCertificateOfIncorporation(request.getCertificateOfIncorporation());
        lender.setMemorandumArticlesOfAssociation(request.getMemorandumArticlesOfAssociation());
        lender.setRegBusinessAddress(request.getRegBusinessAddress());
        lender.setNameApprovalProof(request.getNameApprovalProof());
        lender.setApiKey(ApiKeyUtil.hash(apiKey));

        lenderRepository.save(lender);

        return LenderResponse.builder()
                .apiKey(apiKey)
                .message(String.format(
                                "%s registered successfully. Store your API key safely - it will not be shown again.",
                                request.getLenderName()))
                .build();
    }
}
