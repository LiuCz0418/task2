package com.es.secondhand.controller;

import com.es.secondhand.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传", description = "图片上传接口")
public class UploadController {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @PostMapping("/image")
    @Operation(summary = "上传单张图片")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = saveFile(file);
            return ApiResponse.success("上传成功", url);
        } catch (Exception e) {
            return ApiResponse.error(400, "上传失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/images")
    @Operation(summary = "上传多张图片")
    public ApiResponse<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = saveFile(file);
                urls.add(url);
            }
            return ApiResponse.success("上传成功", urls);
        } catch (Exception e) {
            return ApiResponse.error(400, "上传失败: " + e.getMessage());
        }
    }
    
    private String saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String newFilename = UUID.randomUUID().toString() + extension;
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        File destFile = new File(uploadDir, newFilename);
        file.transferTo(destFile);
        
        return "/uploads/" + newFilename;
    }
    
    /**
     * 图片代理API - 直接返回图片二进制数据
     * 解决微信小程序真机不支持HTTP图片的问题
     */
    @GetMapping("/image-proxy")
    @Operation(summary = "图片代理接口")
    public ResponseEntity<byte[]> getImage(@RequestParam("path") String imagePath) {
        try {
            // 处理路径，移除前导的/uploads/
            String filename = imagePath.replace("/uploads/", "");
            File imageFile = new File(uploadPath, filename);
            
            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // 读取图片文件
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            
            // 根据文件扩展名决定 MIME 类型
            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (filename.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (filename.toLowerCase().endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            }
            
            // 直接返回图片二进制数据
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageBytes);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
