package com.example.zero2dev.services;

import com.example.zero2dev.dtos.*;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.mapper.FrameMapper;
import com.example.zero2dev.models.Frame;
import com.example.zero2dev.models.User;
import com.example.zero2dev.models.UserFrame;
import com.example.zero2dev.repositories.FrameRepository;
import com.example.zero2dev.repositories.UserFrameRepository;
import com.example.zero2dev.repositories.UserRepository;
import com.example.zero2dev.storage.MESSAGE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class FrameService {
    private final FrameRepository frameRepository;
    private final UserFrameRepository userFrameRepository;
    private final FrameMapper mapper;
    private final UserRepository userRepository;

    @Autowired
    public FrameService(FrameRepository frameRepository, UserFrameRepository userFrameRepository,
                        FrameMapper mapper, UserRepository userRepository) {
        this.frameRepository = frameRepository;
        this.userFrameRepository = userFrameRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
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
        userRepository.minusGemsToUser(userId, frame.getPrice());
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

        userFrameRepository.setAllFramesInactiveForUser(userId);

        UserFrame userFrame = userFrameRepository.findByUserIdAndFrameId(userId, frameId)
                .orElse(new UserFrame());

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
    public List<FilterFrameDTO> getFilterList() {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user == null) {
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        List<Object[]> collectData = this.frameRepository.findAllFramesWithCurrentStatus(user.getId());
        List<FilterFrameDTO> frames = mapToFilterFrameDTOList(collectData);

        frames.sort((frame1, frame2) -> Boolean.compare(frame2.getIsOwned(), frame1.getIsOwned()));

        return frames;
    }

    public static List<FilterFrameDTO> mapToFilterFrameDTOList(List<Object[]> queryResults) {
        return queryResults.stream().map(result -> {
            FilterFrameDTO dto = new FilterFrameDTO();
            dto.setFrameId(((Number) result[0]).longValue());
            dto.setName((String) result[1]);
            dto.setDescription((String) result[2]);
            dto.setImageUrl((String) result[3]);
            dto.setFrameType((String) result[4]);
            dto.setCssAnimation((String) result[5]);
            dto.setPrice(((Number) result[6]).longValue());
            dto.setIsDefault((Boolean) result[7]);
            dto.setGems(((Number) result[8]).longValue());
            dto.setIsOwned(((Character) result[9]).equals('1'));
            dto.setIsCurrent(((Character) result[10]).equals('1'));
            return dto;
        }).collect(Collectors.toList());
    }
    public List<UserFrameDTO> getUserFrames(Long userId) {
        return userFrameRepository.findByUserId(userId).stream()
                .map(this::convertToUserFrameDTO)
                .collect(Collectors.toList());
    }
    public FilterFrameDTO getCurrentFrame(Long userId) {
        return userFrameRepository.findFrameDetailsByUsername(userId, true).stream()
                .map(this::mapToFrameDTO)
                .findFirst()
                .orElse(null);
    }


    private UserFrameDTO convertToUserFrameDTO(UserFrame userFrame) {
        UserFrameDTO dto = mapper.toDTO(userFrame);
        dto.setUserId((long) 66771508);
        return dto;
    }
    private FrameDTO convertToFrameDTO(Frame frame){
        return mapper.toDTO(frame);
    }
    private FilterFrameDTO mapToFrameDTO(Object[] row) {
        FilterFrameDTO frameDTO = new FilterFrameDTO();
        frameDTO.setFrameId((Long) row[0]);
        frameDTO.setName((String) row[1]);
        frameDTO.setDescription((String) row[2]);
        frameDTO.setImageUrl((String) row[3]);
        frameDTO.setFrameType((String) row[4]);
        frameDTO.setCssAnimation((String) row[5]);
        frameDTO.setPrice((Long) row[6]);
        frameDTO.setIsDefault((Boolean) row[7]);

        return frameDTO;
    }

}
