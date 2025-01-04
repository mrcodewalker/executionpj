package com.example.zero2dev.services;

import com.example.zero2dev.dtos.CreateFrameRequest;
import com.example.zero2dev.dtos.FrameDTO;
import com.example.zero2dev.dtos.UpdateFrameRequest;
import com.example.zero2dev.dtos.UserFrameDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.mapper.FrameMapper;
import com.example.zero2dev.models.Frame;
import com.example.zero2dev.models.UserFrame;
import com.example.zero2dev.repositories.FrameRepository;
import com.example.zero2dev.repositories.UserFrameRepository;
import com.example.zero2dev.storage.MESSAGE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class FrameService {
    private final FrameRepository frameRepository;
    private final UserFrameRepository userFrameRepository;
    private final FrameMapper mapper;

    @Autowired
    public FrameService(FrameRepository frameRepository, UserFrameRepository userFrameRepository,
                        FrameMapper mapper) {
        this.frameRepository = frameRepository;
        this.userFrameRepository = userFrameRepository;
        this.mapper = mapper;
    }

    public Frame createFrame(CreateFrameRequest request) {
        if (request.getIsDefault() && frameRepository.existsByIsDefaultTrue()) {
            throw new ResourceNotFoundException(MESSAGE.FRAME_EXITS);
        }

        Frame frame = mapper.toEntity(request);
        if (request.getCssAnimation()!=null){
            frame.setCssAnimation(request.getCssAnimation());
        }
        return frameRepository.save(frame);
    }
    public UserFrameDTO purchaseFrame(Long userId, Long frameId) {
        Frame frame = frameRepository.findById(frameId)
                .orElseThrow(() -> new ResourceNotFoundException("Frame not found"));

        if (userFrameRepository.findByUserIdAndFrameId(userId, frameId).isPresent()) {
            throw new ResourceNotFoundException(MESSAGE.FRAME_OWNED);
        }

        UserFrame userFrame = new UserFrame();
        userFrame.setUserId(userId);
        userFrame.setFrame(frame);
        userFrame.setIsActive(false);
        userFrame.setPurchaseDate(LocalDateTime.now());

        UserFrame savedUserFrame = userFrameRepository.save(userFrame);
        return convertToUserFrameDTO(savedUserFrame);
    }
    public UserFrameDTO getActiveFrame(Long userId) {
        return userFrameRepository.findByUserIdAndIsActiveTrue(userId)
                .map(this::convertToUserFrameDTO)
                .orElse(null);
    }
    public UserFrameDTO applyFrame(Long userId, Long frameId) {
        UserFrame userFrame = userFrameRepository.findByUserIdAndFrameId(userId, frameId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.FRAME_NOT_OWNED));

        // Tắt frame đang active (nếu có)
        userFrameRepository.findByUserIdAndIsActiveTrue(userId)
                .ifPresent(activeFrame -> {
                    activeFrame.setIsActive(false);
                    userFrameRepository.save(activeFrame);
                });

        // Kích hoạt frame mới
        userFrame.setIsActive(true);
        UserFrame updatedUserFrame = userFrameRepository.save(userFrame);

        return convertToUserFrameDTO(updatedUserFrame);
    }

    public Frame updateFrame(Long frameId, UpdateFrameRequest request) {
        Frame frame = frameRepository.findById(frameId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.FRAME_NOT_FOUND));
        if (request.getCssAnimation() !=null){
            frame.setCssAnimation(frame.getCssAnimation());
        }
        if (request.getName() != null) frame.setName(request.getName());
        if (request.getDescription() != null) frame.setDescription(request.getDescription());
        if (request.getImageUrl() != null) frame.setImageUrl(request.getImageUrl());
        if (request.getPrice() != null) frame.setPrice(request.getPrice());
        if (request.getIsDefault() != null) {
            if (request.getIsDefault() && frameRepository.existsByIsDefaultTrue()) {
                throw new ResourceNotFoundException(MESSAGE.FRAME_EXITS);
            }
            frame.setIsDefault(request.getIsDefault());
        }

        return frameRepository.save(frame);
    }

    public UserFrame assignFrameToUser(Long userId, Long frameId) {
        Frame frame = frameRepository.findById(frameId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.FRAME_NOT_FOUND));

        userFrameRepository.findByUserIdAndIsActiveTrue(userId)
                .ifPresent(currentFrame -> {
                    currentFrame.setIsActive(false);
                    userFrameRepository.save(currentFrame);
                });

        UserFrame userFrame = new UserFrame();
        userFrame.setUserId(userId);
        userFrame.setFrame(frame);
        userFrame.setIsActive(true);

        return userFrameRepository.save(userFrame);
    }

    public void toggleFrameActive(Long userId, Long frameId) {
        UserFrame userFrame = userFrameRepository.findByUserIdAndFrameId(userId, frameId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.FRAME_NOT_FOUND));

        if (!userFrame.getIsActive()) {
            userFrameRepository.findByUserIdAndIsActiveTrue(userId)
                    .ifPresent(currentFrame -> {
                        currentFrame.setIsActive(false);
                        userFrameRepository.save(currentFrame);
                    });
            userFrame.setIsActive(true);
        } else {
            userFrame.setIsActive(false);
        }

        userFrameRepository.save(userFrame);
    }
    public List<FrameDTO> getFilterList(){
        return frameRepository.findAll().stream()
                .map(this::convertToFrameDTO)
                .collect(Collectors.toList());
    }

    public List<UserFrameDTO> getUserFrames(Long userId) {
        return userFrameRepository.findByUserId(userId).stream()
                .map(this::convertToUserFrameDTO)
                .collect(Collectors.toList());
    }

    private UserFrameDTO convertToUserFrameDTO(UserFrame userFrame) {
        return mapper.toDTO(userFrame);
    }
    private FrameDTO convertToFrameDTO(Frame frame){
        return mapper.toDTO(frame);
    }
}
