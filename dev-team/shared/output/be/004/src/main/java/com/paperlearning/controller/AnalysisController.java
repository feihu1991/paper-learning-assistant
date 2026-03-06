package com.paperlearning.controller;

import com.paperlearning.dto.AnalysisRequestDTO;
import com.paperlearning.dto.AnalysisResultDTO;
import com.paperlearning.entity.AnalysisTask;
import com.paperlearning.entity.PaperAnalysis;
import com.paperlearning.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    
    private final AnalysisService analysisService;
    
    // 创建分析任务
    @PostMapping("/tasks/{paperId}")
    public ResponseEntity<AnalysisTask> createAnalysisTask(@PathVariable Long paperId) {
        AnalysisTask task = analysisService.createAnalysisTask(paperId);
        return ResponseEntity.ok(task);
    }
    
    // 开始分析任务
    @PostMapping("/tasks/{paperId}/start")
    public ResponseEntity<AnalysisTask> startAnalysis(@PathVariable Long paperId) {
        AnalysisTask task = analysisService.startAnalysis(paperId);
        return ResponseEntity.ok(task);
    }
    
    // 保存分析结果
    @PostMapping("/{paperId}")
    public ResponseEntity<PaperAnalysis> saveAnalysisResult(
            @PathVariable Long paperId,
            @RequestBody AnalysisRequestDTO request) {
        PaperAnalysis analysis = analysisService.saveAnalysisResult(paperId, request);
        return ResponseEntity.ok(analysis);
    }
    
    // 获取论文的分析结果
    @GetMapping("/{paperId}")
    public ResponseEntity<AnalysisResultDTO> getAnalysisResult(@PathVariable Long paperId) {
        AnalysisResultDTO result = analysisService.getAnalysisResultDTO(paperId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
    
    // 获取所有分析任务（按状态分组）
    @GetMapping("/tasks/grouped")
    public ResponseEntity<Map<String, List<AnalysisTask>>> getTasksGroupedByStatus() {
        Map<String, List<AnalysisTask>> grouped = analysisService.getTasksGroupedByStatus();
        return ResponseEntity.ok(grouped);
    }
    
    // 获取待分析的任务
    @GetMapping("/tasks/pending")
    public ResponseEntity<List<AnalysisTask>> getPendingTasks() {
        return ResponseEntity.ok(analysisService.getPendingTasks());
    }
    
    // 获取正在分析的任务
    @GetMapping("/tasks/processing")
    public ResponseEntity<List<AnalysisTask>> getProcessingTasks() {
        return ResponseEntity.ok(analysisService.getProcessingTasks());
    }
    
    // 获取已分析的任务
    @GetMapping("/tasks/completed")
    public ResponseEntity<List<AnalysisTask>> getCompletedTasks() {
        return ResponseEntity.ok(analysisService.getCompletedTasks());
    }
    
    // 获取所有任务
    @GetMapping("/tasks")
    public ResponseEntity<List<AnalysisTask>> getAllTasks() {
        return ResponseEntity.ok(analysisService.getTasksByStatus(null));
    }
}
