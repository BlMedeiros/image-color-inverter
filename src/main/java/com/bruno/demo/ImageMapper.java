package com.bruno.demo;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageMapper {


    public byte[] toByte(BufferedImage bufferedImage, String type) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            boolean success = ImageIO.write(bufferedImage, type, baos);

            if (!success) {
                ImageIO.write(bufferedImage, "png", baos);
            }

            return baos.toByteArray();
        }
    }

    public String getFormatFromMultipart(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType != null && contentType.contains("/")) {

            return contentType.split("/")[1];
        }

        return "png";
    }
}
