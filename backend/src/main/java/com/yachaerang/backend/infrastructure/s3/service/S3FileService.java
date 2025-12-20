package com.yachaerang.backend.infrastructure.s3.service;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import com.yachaerang.backend.global.util.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;

    private static final String IMAGE_PATH = "images/profiles/";
    @Value("${external.default-image}")
    private String DEFAULT_IMAGE;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3에 이미지 업로드 메서드
     * @param file
     * @param filename : 미리 가공한 filename (memberId + UUID)
     * @return : 업로드한 url
     */
    public String upload(MultipartFile file, String filename) {
        try {
            String key = IMAGE_PATH + filename;
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            URL url = s3Client.utilities().getUrl(
                    GetUrlRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
            return url.toString();
        } catch (Exception e) {
            throw GeneralException.of(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    /**
     * url 기반으로 삭제
     * @param url
     */
    public void deleteByUrl(String url) {
        if (url == DEFAULT_IMAGE) {
            return;
        }
        URI uri = URI.create(url);
        String path = uri.getPath();

        if (path == null) {
            throw GeneralException.of(ErrorCode.S3_UPLOAD_FAILED);
        }

        String key;
        if (path.startsWith("/" + bucket + "/")) {
            key = path.substring(bucket.length() + 2);
        } else if (path.startsWith("/")) {
            key = path.substring(1);
        } else {
            key = path;
        }

        try {
            s3Client.deleteObject(b -> b.bucket(bucket).key(key));
        } catch (Exception e) {
            LogUtil.error("S3 삭제에 실패했습니다. : {}", url);
        }
    }
}
