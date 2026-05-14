package com.passport.creditpassport.statement.serviceimpl;

import com.passport.creditpassport.exception.InvalidFileException;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import com.passport.creditpassport.service.CloudinaryService;
import com.passport.creditpassport.statement.dto.FinancialStatementResponse;
import com.passport.creditpassport.statement.enums.StatementType;
import com.passport.creditpassport.statement.models.Statement;
import com.passport.creditpassport.statement.repository.FinanceRepository;
import com.passport.creditpassport.statement.service.FinancialStatementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.UnknownNullability;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialStatementServiceImpl implements FinancialStatementService {

    private final CloudinaryService cloudinaryService;
    private final FinanceRepository financeRepository;

    private static final String FOLDER         = "credit-passport/statements";
    private static final long   MAX_SIZE_BYTES = 10 * 1024 * 1024L;

    @Override
    @Transactional
    public FinancialStatementResponse upload(String userId, MultipartFile file, @UnknownNullability StatementType type) {
        validateStatement(file);

        String publicId = "stmt_" + userId + "_" + type.name().toLowerCase()
                + "_" + System.currentTimeMillis();
        String folder = FOLDER + "/" + userId;

        Map<String, Object> result = cloudinaryService.uploadFile(file, folder, publicId);

        Statement statement = Statement.builder()
                .userId(userId)
                .statementType(type)
                .cloudinaryPublicId((String) result.get("public_id"))
                .statementUrl((String) result.get("secure_url"))
                .originalFilename(file.getOriginalFilename())
                // .fileSize()
                .build();

        financeRepository.save(statement);
        log.info("Uploaded {} statement for userId={}", type, userId);
        return FinancialStatementResponse.from(statement);
    }

    @Override
    public List<FinancialStatementResponse> getAllByUser(String userId) {
        // was findById(id) — id undefined, wrong method entirely
        return financeRepository.findByUserId(userId)
                .stream()
                .map(FinancialStatementResponse::from)
                .toList();
    }

    @Override
    public FinancialStatementResponse getById(UUID statementId) {
        // was findById(id) — id undefined; param type was Long, now UUID
        return financeRepository.findById(statementId)
                .map(FinancialStatementResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Statement not found"));
    }

    @Override
    @Transactional
    public void delete(UUID statementId) {
        Statement statement = financeRepository.findById(statementId)
                .orElseThrow(() -> new ResourceNotFoundException("Statement not found"));

        cloudinaryService.deleteFile(statement.getCloudinaryPublicId());
        financeRepository.delete(statement);
    }

    private void validateStatement(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new InvalidFileException("File cannot be empty");
        if (file.getSize() > MAX_SIZE_BYTES)
            throw new InvalidFileException("File exceeds 10MB limit");
        if (!"application/pdf".equals(file.getContentType()) || (!"text/csv".equals(file.getContentType()) || !"application/vnd.ms-excel".equals(file.getContentType()) ||!"\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType())))
            throw new InvalidFileException("Unsupported file type. Allowed: PDF, CSV, XLSX");
    }
}