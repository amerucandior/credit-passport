package com.passport.creditpassport.kyc.serviceimpl;

import com.github.f4b6a3.uuid.UuidCreator;
import com.passport.creditpassport.exception.InvalidFileException;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import com.passport.creditpassport.kyc.dto.KycResponse;
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
    public KycResponse uploadKycDoc(String userId, MultipartFile file) {
        validateKycDocument(file);

        String publicId = "kyc_" + userId + "_doc_" + UuidCreator.getTimeOrderedEpoch();
        String folder = FOLDER + "/" + userId;

        log.info("Uploading KYC document to folder {} for userID = {}", folder, userId);
        Map<String, Object> result = cloudinaryService.uploadFile(file, folder, publicId);
        String uploadedPublicId = (String) result.get("public_id");

        try {
            KYC kyc = KYC.builder()
                    .userId(userId)
                    .cloudinaryPublicId(uploadedPublicId)
                    .kraPin((String) result.get("kra_pin"))
                    .selfiePicture((String) result.get("selfie_picture"))
                    .latestPayslip((String) result.get("latest_payslip"))
                    .nationalIdFront((String) result.get("national_id_front"))
                    .nationalIdBack((String) result.get("national_id_back"))
                    .build();
            kycRepository.save(kyc);
            log.info("Uploaded document to folder {} for userID = {}", folder, userId);
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
    public void deleteKycDoc(String userId) {
        KYC kyc = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC document not found"));

        cloudinaryService.deleteFile(kyc.getCloudinaryPublicId());
        kycRepository.delete(kyc);
    }

    @Override
    @Transactional(readOnly = true)
    public KycResponse getKycDoc(String userId) {
//        return KycRepository.findByUserId(userId) Optional<KYC>
//                .map(KycResponse::from) Optional<KycResponse>
//        .orElseThrow() -> new ResourceNotFoundException("KYC not found")
        return null;
    }

    private void validateKycDocument(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new InvalidFileException("File cannot be empty");
        if (file.getSize() > MAX_SIZE_BYTES)
            throw new InvalidFileException("File exceeds 5MB limit");
    }
}
