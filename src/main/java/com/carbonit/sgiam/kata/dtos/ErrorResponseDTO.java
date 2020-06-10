package com.carbonit.sgiam.kata.dtos;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String code;
    private String message;
}
