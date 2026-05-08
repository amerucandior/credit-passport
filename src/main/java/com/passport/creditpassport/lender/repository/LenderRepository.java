package com.passport.creditpassport.lender.repository;

import com.passport.creditpassport.lender.lendermodel.Lender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LenderRepository extends JpaRepository<Lender, String> {

    boolean existsByCbkLicenseNo(String cbkLicenseNo);
    boolean existsByCertificateOfIncorporation(String certificateOfIncorporation);
    boolean existsByRegBusinessAddress(String regBusinessAddress);
    boolean existsByNameApprovalProof(String nameApprovalProof);
    boolean existsByMemorandumArticlesOfAssociation(String memorandumArticlesOfAssociation);

    Optional<Lender> findByApiKey(String apiKey);
}
