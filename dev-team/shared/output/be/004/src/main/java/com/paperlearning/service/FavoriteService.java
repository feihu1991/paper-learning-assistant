package com.paperlearning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paperlearning.dto.FavoriteRequest;
import com.paperlearning.entity.Favorite;
import com.paperlearning.entity.Paper;
import com.paperlearning.entity.User;
import com.paperlearning.repository.FavoriteRepository;
import com.paperlearning.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    
    private final FavoriteRepository favoriteRepository;
    private final PaperRepository paperRepository;
    
    public Map<String, Object> addFavorite(User currentUser, FavoriteRequest request) {
        // 检查论文是否存在
        Paper paper = paperRepository.selectById(request.getPaperId());
        if (paper == null) {
            throw new RuntimeException("论文不存在");
        }
        
        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, currentUser.getId())
               .eq(Favorite::getPaperId, request.getPaperId());
        if (favoriteRepository.selectCount(wrapper) > 0) {
            throw new RuntimeException("论文已收藏");
        }
        
        // 添加收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(currentUser.getId());
        favorite.setPaperId(request.getPaperId());
        
        favoriteRepository.insert(favorite);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", favorite.getId());
        result.put("paperId", paper.getId());
        result.put("paperTitle", paper.getTitle());
        result.put("createdAt", favorite.getCreatedAt());
        
        return result;
    }
    
    public List<Map<String, Object>> getFavorites(User currentUser) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, currentUser.getId())
               .orderByDesc(Favorite::getCreatedAt);
        
        List<Favorite> favorites = favoriteRepository.selectList(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Paper paper = paperRepository.selectById(favorite.getPaperId());
            if (paper != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", favorite.getId());
                item.put("paperId", paper.getId());
                item.put("paperTitle", paper.getTitle());
                item.put("authors", paper.getAuthors());
                item.put("createdAt", favorite.getCreatedAt());
                result.add(item);
            }
        }
        
        return result;
    }
    
    public void removeFavorite(User currentUser, Long favoriteId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getId, favoriteId)
               .eq(Favorite::getUserId, currentUser.getId());
        
        Favorite favorite = favoriteRepository.selectOne(wrapper);
        if (favorite == null) {
            throw new RuntimeException("收藏记录不存在");
        }
        
        favoriteRepository.deleteById(favoriteId);
    }
}
