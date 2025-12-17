package com.yachaerang.backend.infrastructure.s3.service;

import com.yachaerang.backend.global.exception.GeneralException;
import com.yachaerang.backend.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3FileServiceTest {

    @Mock S3Client s3Client;
    @Mock S3Utilities s3Utilities;
    @InjectMocks S3FileService s3FileService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3FileService, "bucket", "test-bucket");
    }

    @Test
    @DisplayName("업로드 및 URL 반환 성공")
    void 업로드_및_URL_반환_성공() throws Exception {
        // given
        MultipartFile file = mock(MultipartFile.class);
        given(file.getContentType()).willReturn("image/png");
        given(file.getSize()).willReturn(4L);
        given(file.getInputStream()).willReturn(new ByteArrayInputStream(new byte[]{1, 2, 3, 4}));

        given(s3Client.utilities()).willReturn(s3Utilities);
        given(s3Utilities.getUrl(any(GetUrlRequest.class)))
                .willReturn(new URL("https://test-bucket.s3.amazonaws.com/images/profiles/1/abc.png"));

        String filename = "1/abc.png";

        // when
        String url = s3FileService.upload(file, filename);

        // then
        assertThat(url).isEqualTo("https://test-bucket.s3.amazonaws.com/images/profiles/1/abc.png");

        ArgumentCaptor<PutObjectRequest> reqCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(reqCaptor.capture(), any(RequestBody.class));

        PutObjectRequest req = reqCaptor.getValue();
        assertThat(req.bucket()).isEqualTo("test-bucket");
        assertThat(req.key()).isEqualTo("images/profiles/" + filename);
        assertThat(req.contentType()).isEqualTo("image/png");
        assertThat(req.contentLength()).isEqualTo(4L);

        ArgumentCaptor<GetUrlRequest> getUrlCaptor = ArgumentCaptor.forClass(GetUrlRequest.class);
        verify(s3Utilities, times(1)).getUrl(getUrlCaptor.capture());
        assertThat(getUrlCaptor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(getUrlCaptor.getValue().key()).isEqualTo("images/profiles/" + filename);
    }

    @Test
    @DisplayName("업로드 실패 시 S3_UPLOAD_FAILED")
    void 업로드_실패시_예외() throws Exception {
        // given
        MultipartFile file = mock(MultipartFile.class);
        given(file.getContentType()).willReturn("image/png");
        given(file.getSize()).willReturn(10L);
        given(file.getInputStream()).willThrow(new IOException("boom"));

        // when & then
        assertThatThrownBy(() -> s3FileService.upload(file, "1/abc.png"))
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException ex = (GeneralException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.S3_UPLOAD_FAILED);
                });
    }

    @Test
    @DisplayName("URL 기반 삭제 성공")
    void URL_기반_삭제_성공() {
        // given
        String url = "https://s3.amazonaws.com/test-bucket/images/profiles/1/abc.png";
        // when
        s3FileService.deleteByUrl(url);

        // then
        ArgumentCaptor<Consumer<DeleteObjectRequest.Builder>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(s3Client, times(1)).deleteObject(captor.capture());

        DeleteObjectRequest.Builder builder = DeleteObjectRequest.builder();
        captor.getValue().accept(builder);
        DeleteObjectRequest req = builder.build();

        assertThat(req.bucket()).isEqualTo("test-bucket");
        assertThat(req.key()).isEqualTo("images/profiles/1/abc.png");
    }


    @Test
    @DisplayName("삭제 실패 시 예외")
    void 삭제_실패시_예외() {
        // given
        String url = "mailto:test@example.com"; // URL이 null

        // when & then
        assertThatThrownBy(() -> s3FileService.deleteByUrl(url))
                .isInstanceOf(GeneralException.class)
                .satisfies(e -> {
                    GeneralException ex = (GeneralException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.S3_UPLOAD_FAILED);
                });

        verify(s3Client, never()).deleteObject(any(Consumer.class));
    }
}