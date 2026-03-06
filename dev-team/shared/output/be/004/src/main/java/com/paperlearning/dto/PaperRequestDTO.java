package com.paperlearning.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PaperRequestDTO {
    private String title;
    private String abstract_;
    private String authors;
    private String pdfUrl;
    private MultipartFile coverImage;
}
