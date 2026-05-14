package com.passport.creditpassport.serviceimpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.passport.creditpassport.exception.CloudinaryUploadException;
import com.passport.creditpassport.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    @SuppressWarnings("unchecked") // cloudinary returns raw Map. Safe since it returns strings with object values.
    public Map<String, Object> uploadFile(MultipartFile file, String folder, String publicId) {
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", publicId,
                    "resource_type", "auto",
                    "overwrite", true,
                    "invalidate", true
            );
            // Raw Map cast is safe here — Cloudinary always returns Map<String, Object>
            return (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), options);
        } catch (IOException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
            throw new CloudinaryUploadException("Upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked") // cloudinary returns raw Map. Safe since it returns strings with object values.
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    (Map<String, Object>) ObjectUtils.asMap("invalidate", true)
            );
        } catch (IOException e) {
            log.error("Cloudinary delete failed: {}", e.getMessage());
            throw new CloudinaryUploadException("Delete failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateSecureUrl(String publicId) {
        return cloudinary.url().secure(true).generate(publicId);
    }
}
