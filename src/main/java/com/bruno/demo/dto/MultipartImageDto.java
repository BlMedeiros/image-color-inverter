package com.bruno.demo.dto;

import org.springframework.web.multipart.MultipartFile;

public record MultipartImageDto(
        MultipartFile image
) {
}
