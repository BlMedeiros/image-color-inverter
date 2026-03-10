package com.bruno.demo.service;

import com.bruno.demo.ImageMapper;
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

    public ImageService(WasmService wasmService, ImageMapper imageMapper) {
        this.wasmService = wasmService;
        this.imageMapper = imageMapper;
    }

    public  byte[] processInversion(MultipartFile image) throws IOException {
        BufferedImage bufferedImage = removeAlpha(ImageIO.read(image.getInputStream()));

        byte[] imageByte = imageMapper.toByte(bufferedImage,);
        return wasmService.invertImage();
    }

    public BufferedImage removeAlpha(BufferedImage originalImage) {
        BufferedImage rgbImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgbImage.createGraphics();

        try {
            g.setPaint(Color.WHITE);
            g.fillRect(0,0, rgbImage.getWidth(),rgbImage.getHeight());

            g.drawImage(originalImage,0,0,null);
        } finally {
            g.dispose();
        }

        return rgbImage;
    }
}
