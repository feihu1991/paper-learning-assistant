package com.paperlearning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paperlearning.entity.LearningProgress;
import com.paperlearning.repository.LearningProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LearningProgressService {
    
    private final LearningProgressRepository progressRepository;
    
    /**
     * 更新学习进度
     */
    public Map<String, Object> updateProgress(Long userId, Long paperId, String status, Integer progressPercent) {
        LearningProgress progress = progressRepository.findByUserIdAndPaperId(userId, paperId);
        
        if (progress == null) {
            progress = new LearningProgress();
            progress.setUserId(userId);
            progress.setPaperId(paperId);
        }
        
        if (status != null) {
            progress.setStatus(status);
        }
        if (progressPercent != null) {
            progress.setProgressPercent(progressPercent);
        }
        
        progressRepository.insert(progress);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", progress.getId());
        result.put("userId", progress.getUserId());
        result.put("paperId", progress.getPaperId());
        result.put("status", progress.getStatus());
        result.put("progressPercent", progress.getProgressPercent());
        result.put("createdAt", progress.getCreatedAt());
        result.put("updatedAt", progress.getUpdatedAt());
        
        return result;
    }
    
    /**
     * 获取用户学习进度列表
     */
    public List<LearningProgress> getUserProgress(Long userId) {
        return progressRepository.findByUserId(userId);
    }
    
    /**
     * 获取特定论文的学习进度
     */
    public LearningProgress getProgress(Long userId, Long paperId) {
        return progressRepository.findByUserIdAndPaperId(userId, paperId);
    }
}
