package com.paperlearning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paperlearning.dto.PaperDTO;
import com.paperlearning.dto.PaperRequestDTO;
import com.paperlearning.entity.Paper;
import com.paperlearning.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperService {
    
    private final PaperRepository paperRepository;
    
    public Paper createPaper(PaperRequestDTO request) throws IOException {
        Paper paper = new Paper();
        paper.setTitle(request.getTitle());
        paper.setAbstract_(request.getAbstract_());
        paper.setAuthors(request.getAuthors());
        paper.setPdfUrl(request.getPdfUrl());
        
        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            paper.setCoverImage(request.getCoverImage().getBytes());
        }
        
        paperRepository.insert(paper);
        return paper;
    }
    
    public Paper getPaperById(Long id) {
        return paperRepository.selectById(id);
    }
    
    public List<PaperDTO> getAllPapers() {
        List<Paper> papers = paperRepository.selectList(null);
        return papers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public Page<Paper> getPapersByPage(int pageNum, int pageSize) {
        Page<Paper> page = new Page<>(pageNum, pageSize);
        return paperRepository.selectPage(page, null);
    }
    
    public Paper updatePaper(Long id, PaperRequestDTO request) throws IOException {
        Paper paper = paperRepository.selectById(id);
        if (paper == null) {
            throw new RuntimeException("Paper not found");
        }
        
        if (request.getTitle() != null) {
            paper.setTitle(request.getTitle());
        }
        if (request.getAbstract_() != null) {
            paper.setAbstract_(request.getAbstract_());
        }
        if (request.getAuthors() != null) {
            paper.setAuthors(request.getAuthors());
        }
        if (request.getPdfUrl() != null) {
            paper.setPdfUrl(request.getPdfUrl());
        }
        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            paper.setCoverImage(request.getCoverImage().getBytes());
        }
        
        paperRepository.updateById(paper);
        return paper;
    }
    
    public void deletePaper(Long id) {
        paperRepository.deleteById(id);
    }
    
    private PaperDTO convertToDTO(Paper paper) {
        PaperDTO dto = new PaperDTO();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setAbstract_(paper.getAbstract_());
        dto.setAuthors(paper.getAuthors());
        dto.setPdfUrl(paper.getPdfUrl());
        dto.setCreatedAt(paper.getCreatedAt());
        
        if (paper.getCoverImage() != null) {
            dto.setCoverImageBase64(Base64Utils.encodeToString(paper.getCoverImage()));
        }
        
        return dto;
    }
}
