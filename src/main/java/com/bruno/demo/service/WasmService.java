package com.bruno.demo.service;

import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.types.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class WasmService {

    private final Instance instance;
    private final ExportFunction alloc;
    private final ExportFunction dealloc;
    private final ExportFunction invertImageFunc;

    public WasmService() {

        File wasmFile = new File("./image-processor-wasm/target/wasm32-unknown-unknown/release/seu_projeto.wasm");

        this.instance = Instance.builder(Parser.parse(wasmFile)).build();
        this.alloc = instance.export("alloc");
        this.dealloc = instance.export("dealloc");
        this.invertImageFunc = instance.export("invertImage");
    }

    public byte[] invertImage(byte[] image) {
        int len = image.length;

        int ptr = allocateAndWrite(image);

        try {

            invertImageFunc.apply(ptr, len);

            Memory memory = instance.memory();
            return memory.readBytes(ptr,len);

        } finally {
            dealloc.apply(ptr,len);
        }
    }

    private int allocateAndWrite(byte[] image) {
        int ptrValue = (int) alloc.apply(image.length)[0];

        instance.memory().write(ptrValue, image);

        return ptrValue;
    }
}
