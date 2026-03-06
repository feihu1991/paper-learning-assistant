package com.paperlearning.controller;

import com.paperlearning.dto.PaperDTO;
import com.paperlearning.dto.PaperRequestDTO;
import com.paperlearning.entity.Paper;
import com.paperlearning.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class PaperController {
    
    private final PaperService paperService;
    
    @PostMapping
    public ResponseEntity<Paper> createPaper(@ModelAttribute PaperRequestDTO request) throws IOException {
        Paper paper = paperService.createPaper(request);
        return ResponseEntity.ok(paper);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaperDTO> getPaper(@PathVariable Long id) {
        Paper paper = paperService.getPaperById(id);
        if (paper == null) {
            return ResponseEntity.notFound().build();
        }
        
        PaperDTO dto = new PaperDTO();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setAbstract_(paper.getAbstract_());
        dto.setAuthors(paper.getAuthors());
        dto.setPdfUrl(paper.getPdfUrl());
        dto.setCreatedAt(paper.getCreatedAt());
        
        if (paper.getCoverImage() != null) {
            dto.setCoverImageBase64(java.util.Base64.getEncoder().encodeToString(paper.getCoverImage()));
        }
        
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    public ResponseEntity<List<PaperDTO>> getAllPapers() {
        List<PaperDTO> papers = paperService.getAllPapers();
        return ResponseEntity.ok(papers);
    }
    
    @GetMapping("/page")
    public ResponseEntity<List<Paper>> getPapersByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(paperService.getPapersByPage(pageNum, pageSize).getRecords());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Paper> updatePaper(@PathVariable Long id, @ModelAttribute PaperRequestDTO request) throws IOException {
        Paper paper = paperService.updatePaper(id, request);
        return ResponseEntity.ok(paper);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaper(@PathVariable Long id) {
        paperService.deletePaper(id);
        return ResponseEntity.ok().build();
    }
}
