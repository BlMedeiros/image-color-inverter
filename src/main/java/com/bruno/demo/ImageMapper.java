package com.bruno.demo;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageMapper {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public byte[] toByte(BufferedImage bufferedImage, String type) throws IOException {
        ImageIO.write(bufferedImage, type, baos);

        return baos.toByteArray();
    }
}
