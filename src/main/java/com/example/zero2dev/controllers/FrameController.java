package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.CreateFrameRequest;
import com.example.zero2dev.dtos.FrameDTO;
import com.example.zero2dev.dtos.UpdateFrameRequest;
import com.example.zero2dev.dtos.UserFrameDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.mapper.FrameMapper;
import com.example.zero2dev.models.Frame;
import com.example.zero2dev.models.User;
import com.example.zero2dev.models.UserFrame;
import com.example.zero2dev.responses.RestApiResponse;
import com.example.zero2dev.services.FrameService;
import com.example.zero2dev.services.SecurityService;
import com.example.zero2dev.storage.MESSAGE;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/frame")
public class FrameController {
    private final FrameService frameService;
    private final FrameMapper mapper;
    private String uploadDir = "MR.CODEWALKER";
    private FrameController(FrameService frameService,
                            FrameMapper mapper,
                            @Value("${zero2dev.img_path}") String uploadDir){
        this.frameService = frameService;
        this.mapper = mapper;
        this.uploadDir = uploadDir;
    }
    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Path filePath = Paths.get(this.uploadDir, fileName);
        Resource resource = new FileSystemResource(filePath.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private String determineContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
    @PostMapping("/create")
    public ResponseEntity<?> createFrame(@Valid @RequestBody CreateFrameRequest request) {
        Frame frame = frameService.createFrame(request);
        FrameDTO frameDTO = convertToDTO(frame);
        return ResponseEntity.ok(RestApiResponse.success(frameDTO));
    }

    @PutMapping("/{frameId}")
    public ResponseEntity<?> updateFrame(
            @PathVariable Long frameId,
            @RequestBody UpdateFrameRequest request) {
        Frame frame = frameService.updateFrame(frameId, request);
        FrameDTO frameDTO = convertToDTO(frame);
        return ResponseEntity.ok(RestApiResponse.success(frameDTO));
    }

    @PostMapping("/assign/{userId}")
    public ResponseEntity<?> assignFrameToUser(
            @RequestParam("frameId") Long frameId,
            @PathVariable Long userId) {
        UserFrame userFrame = frameService.assignFrameToUser(userId, frameId);
        UserFrameDTO userFrameDTO = convertToUserFrameDTO(userFrame);
        return ResponseEntity.ok(RestApiResponse.success(userFrameDTO));
    }

    @PutMapping("/toggle/{frameId}")
    public ResponseEntity<?> toggleFrameActive(
            @PathVariable Long frameId) {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        frameService.toggleFrameActive(user.getId(), frameId);
        return ResponseEntity.ok(RestApiResponse.success(null));
    }
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseFrame(
            @RequestParam Long frameId) {
            User user = SecurityService.getUserIdFromSecurityContext();
            if (user==null){
                throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
            }
            UserFrameDTO purchasedFrame = frameService.purchaseFrame(user.getId(), frameId);
            return ResponseEntity.ok(RestApiResponse.success(purchasedFrame));
    }
    @PutMapping("/apply")
    public ResponseEntity<?> applyFrame(
            @RequestParam Long frameId) {
            User user = SecurityService.getUserIdFromSecurityContext();
            if (user==null){
                throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
            }
            UserFrameDTO appliedFrame = frameService.applyFrame(user.getId(), frameId);
            return ResponseEntity.ok(RestApiResponse.success(appliedFrame));
    }
    @GetMapping("/active")
    public ResponseEntity<?> getActiveFrame() {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        UserFrameDTO activeFrame = frameService.getActiveFrame(user.getId());
        return ResponseEntity.ok(RestApiResponse.success(activeFrame));
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUserFrames() {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        List<UserFrameDTO> userFrames = frameService.getUserFrames(user.getId());
        return ResponseEntity.ok(RestApiResponse.success(userFrames));
    }
    @GetMapping("/filter")
    public ResponseEntity<?> getFilterFrames() {
        List<FrameDTO> frameDTOS = frameService.getFilterList();
        return ResponseEntity.ok(RestApiResponse.success(frameDTOS));
    }

    private FrameDTO convertToDTO(Frame frame) {
        return mapper.toDTO(frame);
    }

    private UserFrameDTO convertToUserFrameDTO(UserFrame userFrame) {
        return mapper.toDTO(userFrame);
    }
}