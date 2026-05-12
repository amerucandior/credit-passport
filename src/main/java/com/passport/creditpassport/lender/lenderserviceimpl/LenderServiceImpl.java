package com.passport.creditpassport.lender.lenderserviceimpl;

import com.passport.creditpassport.lender.exceptions.LenderAlreadyExistsException;
import com.passport.creditpassport.lender.lenderdto.LenderRequest;
import com.passport.creditpassport.lender.lenderdto.LenderResponse;
import com.passport.creditpassport.lender.lendermodel.Lender;
import com.passport.creditpassport.lender.lenderservice.LenderService;
import com.passport.creditpassport.lender.repository.LenderRepository;
import com.passport.creditpassport.lender.util.ApiKeyUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LenderServiceImpl implements LenderService {

    private final LenderRepository lenderRepository;

    enum RegistrationFailure {
        CBK_LICENSE_NUMBER_ALREADY_EXISTS,
        CERTIFICATE_OF_INCORPORATION_ALREADY_EXISTS,
        REGISTERED_BUSINESS_ADDRESS_ALREADY_EXISTS,
        NAME_APPROVAL_PROOF_ALREADY_EXISTS,
        MEMORANDUM_OF_ASSOCIATION_ALREADY_EXISTS,
        NONE
    }

    private RegistrationFailure checkDuplicate(LenderRequest request) {
        if (lenderRepository.existsByCbkLicenseNo(request.cbkLicenseNo())) {
            return RegistrationFailure.CBK_LICENSE_NUMBER_ALREADY_EXISTS;
        }

        if (request.certificateOfIncorporation() != null &&
                lenderRepository.existsByCertificateOfIncorporation(request.certificateOfIncorporation())) {
            return RegistrationFailure.CERTIFICATE_OF_INCORPORATION_ALREADY_EXISTS;
        }

        if (lenderRepository.existsByRegBusinessAddress(request.regBusinessAddress())) {
            return RegistrationFailure.REGISTERED_BUSINESS_ADDRESS_ALREADY_EXISTS;
        }

        if (request.memorandumArticlesOfAssociation()  != null &&
                lenderRepository.existsByMemorandumArticlesOfAssociation(request.memorandumArticlesOfAssociation())) {
        return RegistrationFailure.MEMORANDUM_OF_ASSOCIATION_ALREADY_EXISTS;
        }


        if (request.nameApprovalProof() != null &&
                lenderRepository.existsByNameApprovalProof(request.nameApprovalProof())) {
            return RegistrationFailure.NAME_APPROVAL_PROOF_ALREADY_EXISTS;
        }
        return RegistrationFailure.NONE;
    }

    @Transactional
    @Override
    public LenderResponse registerLender(LenderRequest request) {
        // Duplicate Checks
        RegistrationFailure failure = checkDuplicate(request);
        if (failure != RegistrationFailure.NONE) {
            String msg = switch (failure) {
                case CBK_LICENSE_NUMBER_ALREADY_EXISTS -> "cbk license number already registered";
                case CERTIFICATE_OF_INCORPORATION_ALREADY_EXISTS ->  "certificate of incorporation already registered";
                case MEMORANDUM_OF_ASSOCIATION_ALREADY_EXISTS ->   "memorandum of incorporation already registered";
                case REGISTERED_BUSINESS_ADDRESS_ALREADY_EXISTS ->   "register business address already registered";
                case NAME_APPROVAL_PROOF_ALREADY_EXISTS ->    "name approval proof already registered";
                case NONE -> throw new IllegalStateException("Unexpected value: " + failure);
            };
            throw new LenderAlreadyExistsException(msg);
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

        try {
            lenderRepository.save(lender);
            log.info("Lender {} registered successfully", request.lenderName());
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate value detected during registration: {}", e.getMessage());
            String msg = e.getMostSpecificCause().getMessage().toLowerCase();
            throw switch (msg) {
                case String m when m.contains("lender_name") -> new LenderAlreadyExistsException(request.lenderName());
                case String m when m.contains("cbk_license_no") -> new LenderAlreadyExistsException(request.cbkLicenseNo());
                case String m when m.contains("certificate_of_incorporation") -> new LenderAlreadyExistsException(request.certificateOfIncorporation());
                case String m when m.contains("memorandum_articles_of_association") -> new LenderAlreadyExistsException(request.memorandumArticlesOfAssociation());
                case String m when m.contains("reg_business_address") -> new LenderAlreadyExistsException(request.regBusinessAddress());
                case String m when m.contains("name_approval_proof") -> new LenderAlreadyExistsException(request.nameApprovalProof());
                default -> new LenderAlreadyExistsException("Lender already exists");
            };
        }

        return LenderResponse.builder()
                .apiKey(apiKey)
                .message(String.format(
                                "%s registered successfully. Store your API key safely - it will not be shown again.",
                                request.lenderName()))
                .build();
    }
}
