package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.LikeDTO;
import com.example.zero2dev.responses.LikeResponse;

public interface ILikeService {
    LikeResponse toggleLike(LikeDTO likeDTO);
    LikeResponse unlike(LikeDTO likeDTO);
}
