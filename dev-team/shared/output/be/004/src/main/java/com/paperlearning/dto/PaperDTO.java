package com.paperlearning.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaperDTO {
    private Long id;
    private String title;
    private String abstract_;
    private String authors;
    private String pdfUrl;
    private String coverImageBase64;
    private LocalDateTime createdAt;
}
