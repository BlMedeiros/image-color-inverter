package com.bruno.demo.service;

import com.bruno.demo.ImageMapper;
import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.types.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class WasmService {

    private final Instance instance;
    private final ExportFunction alloc;
    private final ExportFunction dealloc;
    private final ExportFunction invertImageFunc;
    private final ImageMapper imageMapper;

    public WasmService(ImageMapper imageMapper) {
        File wasmFile = new File("./image-processor-wasm/target/wasm32-unknown-unknown/release/demo.wasm");

        this.instance = Instance.builder(Parser.parse(wasmFile)).build();
        this.alloc = instance.export("alloc");
        this.dealloc = instance.export("dealloc");
        this.invertImageFunc = instance.export("invertImage");

        this.imageMapper = imageMapper;
    }

    public byte[] invertImage(byte[] pixels) throws IOException {

        int ptr = allocateAndWrite(pixels);


        invertImageFunc.apply((long) ptr, (long) pixels.length);

        return instance.memory().readBytes(ptr, pixels.length);
    }

    private int allocateAndWrite(byte[] image) {
        long[] result = alloc.apply((long) image.length);
        int ptrValue = (int) result[0];

        instance.memory().write(ptrValue, image);
        return ptrValue;
    }
}