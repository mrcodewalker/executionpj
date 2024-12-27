package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.UserRankingDTO;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRankingMapper implements RowMapper<UserRankingDTO> {
    @Override
    public UserRankingDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserRankingDTO.builder()
                .ranking(rs.getLong("ranking"))
                .userId(rs.getLong("userId"))
                .username(rs.getString("username"))
                .avatarUrl(rs.getString("avatarUrl"))
                .totalExecutionTime(rs.getLong("totalExecutionTime"))
                .totalMemoryUsed(rs.getLong("totalMemoryUsed"))
                .totalPoints(rs.getLong("totalPoints"))
                .build();
    }
}
