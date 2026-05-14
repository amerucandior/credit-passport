package com.passport.creditpassport.statement.service;

import com.passport.creditpassport.statement.dto.FinancialStatementResponse;
import com.passport.creditpassport.statement.enums.StatementType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FinancialStatementService {
    FinancialStatementResponse upload(String userId, MultipartFile file, StatementType type);
    List<FinancialStatementResponse> getAllByUser(String userId);
    FinancialStatementResponse getById(String userId, UUID id);
    void delete(String userId, UUID id);
}