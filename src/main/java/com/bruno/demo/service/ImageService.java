package com.bruno.demo.service;

import com.bruno.demo.ImageMapper;
import com.bruno.demo.ImagePreprocessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
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

    public byte[] processInversion(MultipartFile image) throws IOException {
        BufferedImage bufferedImage = imagePreprocessor.removeAlpha(ImageIO.read(image.getInputStream()));

        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        byte[] invertedPixels = wasmService.invertImage(pixels);
        bufferedImage.getRaster().setDataElements(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), invertedPixels);

        String format = imageMapper.getFormatFromMultipart(image).toLowerCase();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, format, baos);

        return baos.toByteArray();
    }

}
