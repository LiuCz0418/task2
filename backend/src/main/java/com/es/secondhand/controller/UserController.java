package com.es.secondhand.controller;

import com.es.secondhand.dto.ApiResponse;
import com.es.secondhand.dto.UserDTO;
import com.es.secondhand.entity.User;
import com.es.secondhand.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户注册、登录、信息管理")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<UserDTO.LoginResponse> register(@Valid @RequestBody UserDTO.RegisterRequest request) {
        try {
            UserDTO.LoginResponse response = userService.register(request);
            return ApiResponse.success("注册成功", response);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<UserDTO.LoginResponse> login(@Valid @RequestBody UserDTO.LoginRequest request) {
        try {
            UserDTO.LoginResponse response = userService.login(request);
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息")
    public ApiResponse<UserDTO.UserInfo> getUserInfo(@PathVariable Integer id) {
        try {
            UserDTO.UserInfo userInfo = userService.getUserInfo(id);
            return ApiResponse.success(userInfo);
        } catch (Exception e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }
    
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<UserDTO.UserInfo> getCurrentUserInfo() {
        try {
            User user = userService.getCurrentUser();
            UserDTO.UserInfo userInfo = userService.getUserInfo(user.getId());
            return ApiResponse.success(userInfo);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    @PutMapping("/me")
    @Operation(summary = "更新当前用户信息")
    public ApiResponse<UserDTO.UserInfo> updateCurrentUser(@RequestBody UserDTO.UpdateRequest request) {
        try {
            User user = userService.getCurrentUser();
            UserDTO.UserInfo userInfo = userService.updateUser(user.getId(), request);
            return ApiResponse.success("更新成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
