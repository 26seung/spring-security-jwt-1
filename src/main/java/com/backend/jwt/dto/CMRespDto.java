package com.backend.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CMRespDto<T> {

    private int code;           //  1(성공) / -1(실패)
    private String message; 
    private T data;


    public static <T> CMRespDto successResponse(T data) {
        return CMRespDto.builder()
                .code(1)
                .message("")
                .data(data)
                .build();
    }
    public static CMRespDto errorResponse(String message) {
        return CMRespDto.builder()
                .code(-1)
                .message(message)
                .data(null)
                .build();
    }

}
