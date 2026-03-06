package com.paperlearning.controller;

import com.paperlearning.entity.LearningProgress;
import com.paperlearning.service.LearningProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class LearningProgressController {
    
    private final LearningProgressService progressService;
    
    /**
     * 更新学习进度
     * POST /api/progress/update
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @RequestParam Long paperId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer progressPercent,
            @RequestHeader("X-User-Id") Long userId) {
        
        Map<String, Object> result = progressService.updateProgress(userId, paperId, status, progressPercent);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取用户学习进度列表
     * GET /api/progress
     */
    @GetMapping
    public ResponseEntity<List<LearningProgress>> getUserProgress(
            @RequestHeader("X-User-Id") Long userId) {
        
        List<LearningProgress> progressList = progressService.getUserProgress(userId);
        return ResponseEntity.ok(progressList);
    }
    
    /**
     * 获取特定论文的学习进度
     * GET /api/progress/{paperId}
     */
    @GetMapping("/{paperId}")
    public ResponseEntity<Map<String, Object>> getProgress(
            @PathVariable Long paperId,
            @RequestHeader("X-User-Id") Long userId) {
        
        LearningProgress progress = progressService.getProgress(userId, paperId);
        if (progress == null) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("status", "not_started");
            emptyResult.put("progressPercent", 0);
            return ResponseEntity.ok(emptyResult);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", progress.getId());
        result.put("paperId", progress.getPaperId());
        result.put("status", progress.getStatus());
        result.put("progressPercent", progress.getProgressPercent());
        result.put("createdAt", progress.getCreatedAt());
        result.put("updatedAt", progress.getUpdatedAt());
        
        return ResponseEntity.ok(result);
    }
}
