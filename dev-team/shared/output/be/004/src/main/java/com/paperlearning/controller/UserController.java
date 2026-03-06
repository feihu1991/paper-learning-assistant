package com.paperlearning.controller;

import com.paperlearning.dto.UserProfileDTO;
import com.paperlearning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取用户信息
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(
            @RequestHeader("X-User-Id") Long userId) {
        
        UserProfileDTO profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
    
    /**
     * 更新用户信息
     * PUT /api/user/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String avatarUrl) {
        
        UserProfileDTO profile = userService.updateUserProfile(userId, displayName, bio, avatarUrl);
        return ResponseEntity.ok(profile);
    }
}
