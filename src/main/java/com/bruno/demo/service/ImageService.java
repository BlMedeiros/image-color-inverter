package com.bruno.demo.service;

import com.bruno.demo.ImageMapper;
import com.bruno.demo.ImagePreprocessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class ImageService {

    private final WasmService wasmService;
    private final ImageMapper imageMapper;
    private final ImagePreprocessor imagePreprocessor;

    public ImageService(WasmService wasmService, ImageMapper imageMapper, ImagePreprocessor imagePreprocessor) {
        this.wasmService = wasmService;
        this.imageMapper = imageMapper;
        this.imagePreprocessor = imagePreprocessor;
    }

    public  byte[] processInversion(MultipartFile image) throws IOException {
        BufferedImage bufferedImage = imagePreprocessor.removeAlpha(ImageIO.read(image.getInputStream()));

        String imageFormat = imageMapper.getFormatFromMultipart(image);

        byte[] imageByte = imageMapper.toByte(bufferedImage,imageFormat);

        return wasmService.invertImage(imageByte);
    }
}
