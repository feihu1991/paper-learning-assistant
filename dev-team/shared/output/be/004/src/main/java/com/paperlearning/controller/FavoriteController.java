package com.paperlearning.controller;

import com.paperlearning.dto.FavoriteRequest;
import com.paperlearning.entity.User;
import com.paperlearning.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@AuthenticationPrincipal User currentUser,
                                          @RequestBody FavoriteRequest request) {
        try {
            Map<String, Object> result = favoriteService.addFavorite(currentUser, request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal User currentUser) {
        try {
            List<Map<String, Object>> favorites = favoriteService.getFavorites(currentUser);
            return ResponseEntity.ok(favorites);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFavorite(@AuthenticationPrincipal User currentUser,
                                             @PathVariable Long id) {
        try {
            favoriteService.removeFavorite(currentUser, id);
            return ResponseEntity.ok(Map.of("message", "取消收藏成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
