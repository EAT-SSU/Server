package ssu.eatssu.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URL;
import java.io.File;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3UploaderTest {

    @Test
    void 파일을_S3에_올리고_URL을_반환한다() throws Exception {
        AmazonS3Client s3Client = mock(AmazonS3Client.class);
        when(s3Client.getUrl(eq("bucket"), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(new URL("https://example.com/image.png"));
        S3Uploader uploader = new S3Uploader(s3Client);
        ReflectionTestUtils.setField(uploader, "bucket", "bucket");

        String url = uploader.upload(new MockMultipartFile("image", "image.png", "image/png", "image".getBytes()), "reviews");

        assertThat(url).isEqualTo("https://example.com/image.png");
        verify(s3Client).putObject(any());
    }

    @Test
    void 임시_파일_삭제에_실패해도_업로드_결과는_유지한다() {
        S3Uploader uploader = new S3Uploader(mock(AmazonS3Client.class));
        File file = mock(File.class);
        when(file.delete()).thenReturn(false);

        ReflectionTestUtils.invokeMethod(uploader, "removeNewFile", file);

        verify(file).delete();
    }

    @Test
    void 같은_이름의_임시_파일이_있으면_업로드하지_않는다() throws Exception {
        File file = mock(File.class);
        when(file.createNewFile()).thenReturn(false);
        S3Uploader uploader = new S3Uploader(mock(AmazonS3Client.class)) {
            @Override
            File createFile(String fileName) {
                return file;
            }
        };

        assertThatThrownBy(() -> uploader.upload(new MockMultipartFile("image", "image.png", "image/png", new byte[0]), "reviews"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
