package com.bruno.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class ImageService {

    WasmService wasmService;

    public ImageService(WasmService wasmService) {
        this.wasmService = wasmService;
    }

    public  byte[] processInversion(MultipartFile image) throws IOException {
        BufferedImage bufferedImage = removeAlpha(ImageIO.read(image.getInputStream()));

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
