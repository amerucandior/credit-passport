package com.passport.creditpassport.kyc.serviceimpl;

import com.github.f4b6a3.uuid.UuidCreator;
import com.passport.creditpassport.exception.InvalidFileException;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import com.passport.creditpassport.kyc.dto.KycResponse;
import com.passport.creditpassport.kyc.enums.KycDocType;
import com.passport.creditpassport.kyc.model.KYC;
import com.passport.creditpassport.kyc.repository.KycRepository;
import com.passport.creditpassport.kyc.service.KycService;
import com.passport.creditpassport.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {

    private final KycRepository kycRepository;
    private final CloudinaryService cloudinaryService;

    private static final String FOLDER         = "credit-passport/kyc";
    private static final long MAX_SIZE_BYTES   = 5 * 1024 * 1024L;

    @Override
    @Transactional
    public KycResponse uploadKycDoc(String userId, MultipartFile file, KycDocType kycDocType) {
        validateKycDocument(file);

        String publicId = "kyc_" + userId + "_doc_" + UuidCreator.getTimeOrderedEpoch();
        String folder = FOLDER + "/" + kycDocType + userId;

        log.info("Uploading KYC document to folder {} for userID = {}", folder, userId);
        Map<String, Object> result = cloudinaryService.uploadFile(file, folder, publicId);
        String uploadedPublicId = (String) result.get("public_id");

        try {
            KYC kyc = kycRepository.findByUserId(userId)
                    .orElse(KYC.builder().userId(userId).build());

            switch (kycDocType) {
                case SELFIE            -> {
                    kyc.setSelfiePicture(
                        cloudinaryService.generateSecureUrl(uploadedPublicId));
                    kyc.setSelfiePublicId(uploadedPublicId);
                }
                case NATIONAL_ID_FRONT -> {
                    kyc.setNationalIdFront(
                        cloudinaryService.generateSecureUrl(uploadedPublicId));
                    kyc.setNationalIdFrontPublicId(uploadedPublicId);
                }
                case NATIONAL_ID_BACK  -> {
                    kyc.setNationalIdBack(
                        cloudinaryService.generateSecureUrl(uploadedPublicId));
                    kyc.setNationalIdBackPublicId(uploadedPublicId);
                }
                case KRA_PIN           -> {
                    kyc.setKraPin(
                        cloudinaryService.generateSecureUrl(uploadedPublicId));
                    kyc.setKraPinPublicId(uploadedPublicId);
                }
                case PAYSLIP           -> {
                    kyc.setLatestPayslip(
                        cloudinaryService.generateSecureUrl(uploadedPublicId));
                    kyc.setLatestPayslipPublicId(uploadedPublicId);
                }
            }
            kycRepository.save(kyc);
            log.info("Uploaded KYC {} for userID = {}", kycDocType, userId);
            return KycResponse.from(kyc);
        } catch (RuntimeException e) {
            try {
                cloudinaryService.deleteFile(uploadedPublicId);
            } catch (Exception suppress) {
                log.warn("Failed to delete file {} for userID = {}", uploadedPublicId, userId, e);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteKycDoc(String userId, KycDocType kycDocType) {
        KYC kyc = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC document not found"));

        String publicId = switch (kycDocType) {
            case SELFIE            -> kyc.getSelfiePublicId();
            case NATIONAL_ID_FRONT -> kyc.getNationalIdFrontPublicId();
            case NATIONAL_ID_BACK  -> kyc.getNationalIdBackPublicId();
            case KRA_PIN           -> kyc.getKraPinPublicId();
            case PAYSLIP           -> kyc.getLatestPayslipPublicId();
        };

        if (publicId == null) {
            throw new ResourceNotFoundException(kycDocType + " has not been uploaded");
        }

        cloudinaryService.deleteFile(publicId);

        switch (kycDocType) {
            case SELFIE            -> { kyc.setSelfiePicture(null);    kyc.setSelfiePublicId(null); }
            case NATIONAL_ID_FRONT -> { kyc.setNationalIdFront(null);  kyc.setNationalIdFrontPublicId(null); }
            case NATIONAL_ID_BACK  -> { kyc.setNationalIdBack(null);   kyc.setNationalIdBackPublicId(null); }
            case KRA_PIN           -> { kyc.setKraPin(null);           kyc.setKraPinPublicId(null); }
            case PAYSLIP           -> { kyc.setLatestPayslip(null);    kyc.setLatestPayslipPublicId(null); }
        }

        kycRepository.save(kyc);
    }

    @Override
    @Transactional(readOnly = true)
    public KycResponse getKycDoc(String userId) {
        return kycRepository.findByUserId(userId)
                .map(KycResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("KYC not found for user: " + userId));
    }

    private void validateKycDocument(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new InvalidFileException("File cannot be empty");
        if (file.getSize() > MAX_SIZE_BYTES)
            throw new InvalidFileException("File exceeds 5MB limit");
    }
}
