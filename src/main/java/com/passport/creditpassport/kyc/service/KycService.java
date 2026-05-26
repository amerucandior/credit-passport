package com.passport.creditpassport.kyc.service;

import com.passport.creditpassport.kyc.dto.KycResponse;
import com.passport.creditpassport.kyc.enums.KycDocType;
import org.springframework.web.multipart.MultipartFile;

public interface KycService {
    KycResponse uploadKycDoc(String userId, MultipartFile file, KycDocType kycDocType);
    void deleteKycDoc(String userId);
    KycResponse getKycDoc(String userId);
}
