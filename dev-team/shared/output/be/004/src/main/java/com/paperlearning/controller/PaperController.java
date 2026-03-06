package com.paperlearning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperlearning.dto.PaperDTO;
import com.paperlearning.dto.PaperRequestDTO;
import com.paperlearning.entity.Paper;
import com.paperlearning.entity.PaperAnalysis;
import com.paperlearning.repository.PaperAnalysisRepository;
import com.paperlearning.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class PaperController {
    
    private final PaperService paperService;
    private final PaperAnalysisRepository analysisRepository;
    private final ObjectMapper objectMapper;
    
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
    
    /**
     * 导出论文分析报告
     * GET /api/papers/{id}/export?format=pdf|markdown
     */
    @GetMapping("/{id}/export")
    public ResponseEntity<?> exportPaper(
            @PathVariable Long id,
            @RequestParam(defaultValue = "markdown") String format) {
        
        Paper paper = paperService.getPaperById(id);
        if (paper == null) {
            return ResponseEntity.notFound().build();
        }
        
        PaperAnalysis analysis = analysisRepository.findByPaperId(id);
        
        if ("pdf".equalsIgnoreCase(format)) {
            // 返回PDF格式（简化为text，真实场景需要PDF库）
            String content = generateMarkdownReport(paper, analysis);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id + "_report.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content.getBytes());
        } else {
            // 默认返回markdown格式
            String content = generateMarkdownReport(paper, analysis);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id + "_report.md\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(content);
        }
    }
    
    /**
     * 生成Markdown格式的报告
     */
    private String generateMarkdownReport(Paper paper, PaperAnalysis analysis) {
        StringBuilder sb = new StringBuilder();
        
        // 标题
        sb.append("# ").append(paper.getTitle()).append("\n\n");
        
        // 作者
        if (paper.getAuthors() != null) {
            sb.append("**作者:** ").append(paper.getAuthors()).append("\n\n");
        }
        
        // 摘要
        if (paper.getAbstract_() != null) {
            sb.append("## 摘要\n\n").append(paper.getAbstract_()).append("\n\n");
        }
        
        // 分析结果
        if (analysis != null) {
            // 背景
            if (analysis.getBackground() != null) {
                sb.append("## 背景介绍\n\n").append(analysis.getBackground()).append("\n\n");
            }
            
            // 核心概念
            if (analysis.getCoreConcepts() != null) {
                sb.append("## 核心概念\n\n");
                try {
                    List<String> concepts = objectMapper.readValue(analysis.getCoreConcepts(), List.class);
                    for (String concept : concepts) {
                        sb.append("- ").append(concept).append("\n");
                    }
                } catch (Exception e) {
                    sb.append(analysis.getCoreConcepts());
                }
                sb.append("\n");
            }
            
            // 方法论
            if (analysis.getMethodology() != null) {
                sb.append("## 方法论\n\n");
                try {
                    List<String> methods = objectMapper.readValue(analysis.getMethodology(), List.class);
                    for (String method : methods) {
                        sb.append("- ").append(method).append("\n");
                    }
                } catch (Exception e) {
                    sb.append(analysis.getMethodology());
                }
                sb.append("\n");
            }
            
            // 关键创新
            if (analysis.getKeyInnovations() != null) {
                sb.append("## 关键创新\n\n");
                try {
                    List<String> innovations = objectMapper.readValue(analysis.getKeyInnovations(), List.class);
                    for (String innovation : innovations) {
                        sb.append("- ").append(innovation).append("\n");
                    }
                } catch (Exception e) {
                    sb.append(analysis.getKeyInnovations());
                }
                sb.append("\n");
            }
            
            // 学习路径
            if (analysis.getLearningPath() != null) {
                sb.append("## 学习路径\n\n");
                try {
                    List<String> path = objectMapper.readValue(analysis.getLearningPath(), List.class);
                    for (int i = 0; i < path.size(); i++) {
                        sb.append(i + 1).append(". ").append(path.get(i)).append("\n");
                    }
                } catch (Exception e) {
                    sb.append(analysis.getLearningPath());
                }
                sb.append("\n");
            }
            
            // 难度和时间
            if (analysis.getDifficulty() != null) {
                sb.append("## 难度评估\n\n");
                sb.append("难度等级: ").append(analysis.getDifficulty()).append("/5\n\n");
            }
            
            if (analysis.getEstimatedTime() != null) {
                sb.append("预估学习时间: ").append(analysis.getEstimatedTime()).append(" 分钟\n\n");
            }
        }
        
        sb.append("---\n\n");
        sb.append("*Generated by Paper Learning System*\n");
        
        return sb.toString();
    }
}
