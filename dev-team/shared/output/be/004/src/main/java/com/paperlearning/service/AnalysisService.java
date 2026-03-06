package com.paperlearning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperlearning.dto.AnalysisRequestDTO;
import com.paperlearning.dto.AnalysisResultDTO;
import com.paperlearning.entity.AnalysisTask;
import com.paperlearning.entity.Paper;
import com.paperlearning.entity.PaperAnalysis;
import com.paperlearning.repository.AnalysisTaskRepository;
import com.paperlearning.repository.PaperAnalysisRepository;
import com.paperlearning.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    
    private final PaperRepository paperRepository;
    private final PaperAnalysisRepository paperAnalysisRepository;
    private final AnalysisTaskRepository analysisTaskRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public AnalysisTask createAnalysisTask(Long paperId) {
        // 检查论文是否存在
        Paper paper = paperRepository.selectById(paperId);
        if (paper == null) {
            throw new RuntimeException("Paper not found");
        }
        
        // 创建分析任务
        AnalysisTask task = new AnalysisTask();
        task.setPaperId(paperId);
        task.setStatus("pending");
        analysisTaskRepository.insert(task);
        
        return task;
    }
    
    @Transactional
    public PaperAnalysis saveAnalysisResult(Long paperId, AnalysisRequestDTO request) {
        // 更新任务状态为已完成
        QueryWrapper<AnalysisTask> taskQuery = new QueryWrapper<>();
        taskQuery.eq("paper_id", paperId);
        List<AnalysisTask> tasks = analysisTaskRepository.selectList(taskQuery);
        
        if (!tasks.isEmpty()) {
            AnalysisTask task = tasks.get(0);
            task.setStatus("completed");
            analysisTaskRepository.updateById(task);
        }
        
        // 保存分析结果
        PaperAnalysis analysis = new PaperAnalysis();
        analysis.setPaperId(paperId);
        analysis.setBackground(request.getBackground());
        analysis.setDifficulty(request.getDifficulty());
        analysis.setEstimatedTime(request.getEstimatedTime());
        analysis.setStatus("completed");
        
        try {
            if (request.getCoreConcepts() != null) {
                analysis.setCoreConcepts(objectMapper.writeValueAsString(request.getCoreConcepts()));
            }
            if (request.getMethodology() != null) {
                analysis.setMethodology(objectMapper.writeValueAsString(request.getMethodology()));
            }
            if (request.getKeyInnovations() != null) {
                analysis.setKeyInnovations(objectMapper.writeValueAsString(request.getKeyInnovations()));
            }
            if (request.getLearningPath() != null) {
                analysis.setLearningPath(objectMapper.writeValueAsString(request.getLearningPath()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON", e);
        }
        
        paperAnalysisRepository.insert(analysis);
        
        return analysis;
    }
    
    public PaperAnalysis getAnalysisByPaperId(Long paperId) {
        QueryWrapper<PaperAnalysis> query = new QueryWrapper<>();
        query.eq("paper_id", paperId);
        return paperAnalysisRepository.selectOne(query);
    }
    
    public AnalysisResultDTO getAnalysisResultDTO(Long paperId) {
        PaperAnalysis analysis = getAnalysisByPaperId(paperId);
        if (analysis == null) {
            return null;
        }
        return convertToDTO(analysis);
    }
    
    public List<AnalysisTask> getTasksByStatus(String status) {
        QueryWrapper<AnalysisTask> query = new QueryWrapper<>();
        query.eq("status", status);
        query.orderByDesc("created_at");
        return analysisTaskRepository.selectList(query);
    }
    
    public Map<String, List<AnalysisTask>> getTasksGroupedByStatus() {
        List<AnalysisTask> allTasks = analysisTaskRepository.selectList(null);
        
        return allTasks.stream()
                .collect(Collectors.groupingBy(AnalysisTask::getStatus));
    }
    
    public List<AnalysisTask> getPendingTasks() {
        return getTasksByStatus("pending");
    }
    
    public List<AnalysisTask> getProcessingTasks() {
        return getTasksByStatus("processing");
    }
    
    public List<AnalysisTask> getCompletedTasks() {
        return getTasksByStatus("completed");
    }
    
    @Transactional
    public AnalysisTask startAnalysis(Long paperId) {
        QueryWrapper<AnalysisTask> query = new QueryWrapper<>();
        query.eq("paper_id", paperId);
        List<AnalysisTask> tasks = analysisTaskRepository.selectList(query);
        
        AnalysisTask task;
        if (tasks.isEmpty()) {
            // 创建新任务
            task = new AnalysisTask();
            task.setPaperId(paperId);
            task.setStatus("processing");
            analysisTaskRepository.insert(task);
        } else {
            // 更新现有任务
            task = tasks.get(0);
            task.setStatus("processing");
            analysisTaskRepository.updateById(task);
        }
        
        return task;
    }
    
    private AnalysisResultDTO convertToDTO(PaperAnalysis analysis) {
        AnalysisResultDTO dto = new AnalysisResultDTO();
        dto.setId(analysis.getId());
        dto.setPaperId(analysis.getPaperId());
        dto.setBackground(analysis.getBackground());
        dto.setDifficulty(analysis.getDifficulty());
        dto.setEstimatedTime(analysis.getEstimatedTime());
        dto.setStatus(analysis.getStatus());
        
        try {
            if (analysis.getCoreConcepts() != null) {
                dto.setCoreConcepts(objectMapper.readValue(analysis.getCoreConcepts(), 
                    new TypeReference<List<String>>() {}));
            }
            if (analysis.getMethodology() != null) {
                dto.setMethodology(objectMapper.readValue(analysis.getMethodology(), 
                    new TypeReference<List<String>>() {}));
            }
            if (analysis.getKeyInnovations() != null) {
                dto.setKeyInnovations(objectMapper.readValue(analysis.getKeyInnovations(), 
                    new TypeReference<List<String>>() {}));
            }
            if (analysis.getLearningPath() != null) {
                dto.setLearningPath(objectMapper.readValue(analysis.getLearningPath(), 
                    new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
        
        return dto;
    }
}
