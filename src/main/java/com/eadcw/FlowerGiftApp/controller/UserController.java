package com.eadcw.FlowerGiftApp.controller;

import com.eadcw.FlowerGiftApp.dto.UserDTO;
import com.eadcw.FlowerGiftApp.dto.UpdateUserProfileRequest;
import com.eadcw.FlowerGiftApp.entity.User;
import com.eadcw.FlowerGiftApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUser(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorMap(e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UpdateUserProfileRequest request) {
        try {
            UserDTO updatedUser = userService.updateUser(userId, request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorMap(e.getMessage()));
        }
    }

    private Map<String, String> createErrorMap(String error) {
        Map<String, String> map = new HashMap<>();
        map.put("error", error);
        return map;
    }
}