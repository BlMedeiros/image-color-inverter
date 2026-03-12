package com.bruno.demo;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class ImagePreprocessor {

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
