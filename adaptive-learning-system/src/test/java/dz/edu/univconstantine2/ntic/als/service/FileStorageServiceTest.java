package dz.edu.univconstantine2.ntic.als.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;











@ExtendWith(MockitoExtension.class)
@DisplayName("FileStorageService unit tests")
class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    



    private static final byte[] MINIMAL_PDF_BYTES = (
            "%PDF-1.4\n1 0 obj\n<< /Type /Catalog >>\nendobj\nxref\n0 0\ntrailer\n<< >>\n%%EOF"
    ).getBytes();

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
        fileStorageService.init();
    }

    

    @Test
    @DisplayName("Filename containing '..' throws SecurityException")
    void store_dotDotInFilename_throwsSecurityException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "../evil.pdf", "application/pdf", MINIMAL_PDF_BYTES);

        assertThatThrownBy(() -> fileStorageService.store(file))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid filename");
    }

    @Test
    @DisplayName("Filename containing '/' throws SecurityException")
    void store_slashInFilename_throwsSecurityException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "dir/evil.pdf", "application/pdf", MINIMAL_PDF_BYTES);

        assertThatThrownBy(() -> fileStorageService.store(file))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid filename");
    }

    

    @Test
    @DisplayName("Disallowed extension (.exe) throws SecurityException")
    void store_invalidExtension_throwsSecurityException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "malware.exe", "application/octet-stream",
                new byte[]{0x4D, 0x5A}); 

        assertThatThrownBy(() -> fileStorageService.store(file))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("not permitted");
    }

    

    @Test
    @DisplayName("Valid PDF filename results in a UUID-based stored filename")
    void store_validPdf_returnsUuidFilename() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "lecture-notes.pdf", "application/pdf", MINIMAL_PDF_BYTES);

        String stored = fileStorageService.store(file);

        assertThat(stored).isNotNull();
        
        assertThat(stored).matches("[0-9a-f\\-]{36}\\.pdf");
    }

    @Test
    @DisplayName("Stored filename ends with '.pdf'")
    void store_validPdf_storedFilenameEndsWith_pdf() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "slides.pdf", "application/pdf", MINIMAL_PDF_BYTES);

        String stored = fileStorageService.store(file);

        assertThat(stored).endsWith(".pdf");
    }
}
