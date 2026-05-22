package com.passport.creditpassport.statement.serviceimpl;

import com.github.f4b6a3.uuid.UuidCreator;
import com.passport.creditpassport.exception.InvalidFileException;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import com.passport.creditpassport.service.CloudinaryService;
import com.passport.creditpassport.statement.dto.FinancialStatementResponse;
import com.passport.creditpassport.statement.enums.StatementType;
import com.passport.creditpassport.statement.models.Statement;
import com.passport.creditpassport.statement.repository.FinanceRepository;
import com.passport.creditpassport.statement.service.FinancialStatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public FinancialStatementResponse upload(String userId, MultipartFile file, @NotNull StatementType type) {
        validateStatement(file);

        String publicId = "stmt_" + userId + "_" + type.name().toLowerCase()
                + "_" + UuidCreator.getTimeOrderedEpoch();
        String folder = FOLDER + "/" + userId;

        log.info("Uploading {} statement for userId={}", type, userId);
        Map<String, Object> result = cloudinaryService.uploadFile(file, folder, publicId);
        String uploadedPublicId = (String) result.get("public_id");

        try {
            Statement statement = Statement.builder()
                    .userId(userId)
                    .statementType(type)
                    .cloudinaryPublicId(uploadedPublicId)
                    .statementUrl((String) result.get("secure_url"))
                    .originalFilename(file.getOriginalFilename())
                    .build();
            financeRepository.save(statement);
            log.info("Uploaded {} statement for userId={}", type, userId);
            return FinancialStatementResponse.from(statement);
        } catch (RuntimeException e) {
            try {
                cloudinaryService.deleteFile(uploadedPublicId);
            } catch (Exception suppress) {
                log.warn("Failed to clean up orphaned Cloudinary file {}", uploadedPublicId, suppress);
            }
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<FinancialStatementResponse> getAllByUser(String userId) {
        return financeRepository.findByUserId(userId)
                .stream()
                .map(FinancialStatementResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialStatementResponse getById(String userId, UUID id) {
        return financeRepository.findByIdAndUserId(id, userId)
                .map(FinancialStatementResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Statement not found"));
    }

    @Override
    @Transactional
    public void delete(String userId, UUID id) {
        Statement statement = financeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Statement not found"));

        cloudinaryService.deleteFile(statement.getCloudinaryPublicId());
        financeRepository.delete(statement);
    }

    private void validateStatement(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new InvalidFileException("File cannot be empty");
        if (file.getSize() > MAX_SIZE_BYTES)
            throw new InvalidFileException("File exceeds 10MB limit");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new InvalidFileException("Unsupported file type. Allowed: PDF, CSV, XLSX, XLS");
    }

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "text/csv",
            "application/vnd.ms-excel",
            "application/octet-stream",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );
}