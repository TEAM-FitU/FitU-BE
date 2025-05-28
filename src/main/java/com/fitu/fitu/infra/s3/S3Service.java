package com.fitu.fitu.infra.s3;

import com.fitu.fitu.global.error.ErrorCode;
import com.fitu.fitu.global.error.exception.BusinessException;
import com.fitu.fitu.global.error.exception.SystemException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Template s3Template;

    public String upload(final MultipartFile file, final String dirName) {
        try {
            final String key = generateKey(file, dirName);

            final ObjectMetadata objectMetadata = new ObjectMetadata.Builder()
                    .contentType(file.getContentType())
                    .build();

            final S3Resource s3Resource = s3Template.upload(bucket, key, file.getInputStream(), objectMetadata);

            return s3Resource.getURL().toString();
        } catch (IOException | S3Exception e) {
            throw new SystemException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    public void delete(final String imageUrl) {
        try {
            final String key = extractKey(imageUrl);

            s3Template.deleteObject(bucket, key);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.S3_DELETE_FAILED);
        }
    }

    private String generateKey(final MultipartFile file, final String dirName) {
        final String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        final String fileName = UUID.randomUUID() + "." + extension;
        final String key = dirName + fileName;

        return key;
    }

    private String extractKey(final String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            return uri.getPath().substring(1);
        } catch (URISyntaxException e) {
            throw new BusinessException(ErrorCode.S3_INVALID_URL);
        }
    }
}