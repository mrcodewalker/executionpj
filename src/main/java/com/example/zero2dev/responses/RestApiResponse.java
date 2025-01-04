package com.example.zero2dev.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> RestApiResponse<T> success(T data) {
        return new RestApiResponse<>(true, "Success", data);
    }

    public static <T> RestApiResponse<T> error(String message) {
        return new RestApiResponse<>(false, message, null);
    }
}
