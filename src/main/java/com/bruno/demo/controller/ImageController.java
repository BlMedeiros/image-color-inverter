package com.bruno.demo.controller;

import com.bruno.demo.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

@RestController
@RequestMapping("/image")
public class ImageController {

    ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/invert", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getInvertedImage(@RequestBody MultipartFile image) throws IOException {
        return imageService.processInversion(image);
    }
}
