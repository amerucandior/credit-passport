package com.passport.creditpassport.kyc.service;

import com.passport.creditpassport.kyc.dto.KycResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface KycService {
    KycResponse uploadKycDoc(String userId, MultipartFile file);
    void deleteKycDoc(String userId);
    KycResponse getKycDoc(String userId);
}
