use std::mem;

#[unsafe(no_mangle)]
pub extern "C" fn alloc(capacity: usize) -> *mut u8 {
    let mut buf = Vec::with_capacity(capacity);
    let ptr = buf.as_mut_ptr();
    mem::forget(buf);
    ptr
}

#[unsafe(no_mangle)]
pub extern "C" fn dealloc(ptr: *mut u8, capacity: usize) {
    unsafe {
        let _ = Vec::from_raw_parts(ptr, 0, capacity);
    }
}

#[unsafe(no_mangle)]
pub extern "C" fn invertImage(ptr: *mut u8, len: usize) {
    unsafe {
        let slice = std::slice::from_raw_parts_mut(ptr, len);

        for byte in slice.iter_mut() {
            *byte = 255 - *byte;
        }
    }
}

