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

        BufferedImage imgByte = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        imgByte.getGraphics().drawImage(bufferedImage, 0, 0, null);

        byte[] pixels = ((DataBufferByte) imgByte.getRaster().getDataBuffer()).getData();
        byte[] invertedPixels = wasmService.invertImage(pixels);
        imgByte.getRaster().setDataElements(0, 0, imgByte.getWidth(), imgByte.getHeight(), invertedPixels);

        String format = imageMapper.getFormatFromMultipart(image).toLowerCase();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imgByte, format, baos);

        return baos.toByteArray();
    }

}
