package com.example.zero2dev.responses;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.errors = new ArrayList<>();
    }
}